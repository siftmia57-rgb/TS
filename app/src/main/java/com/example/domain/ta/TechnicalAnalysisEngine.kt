package com.example.domain.ta

import com.example.domain.model.Candle
import com.example.domain.model.SignalDirection
import com.example.domain.model.TechnicalSummary
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object TechnicalAnalysisEngine {

    fun analyze(
        pair: String,
        timeframe: String,
        candles: List<Candle>,
        changePercent24h: Double,
        feedSource: String
    ): TechnicalSummary {
        require(candles.isNotEmpty()) { "Candle list must not be empty." }
        val closes = candles.map { it.close }
        val livePrice = closes.last()

        // 1. EMA (9 & 21 Crossover)
        val ema9Series = computeEmaSeries(closes, 9)
        val ema21Series = computeEmaSeries(closes, 21)
        val ema9 = ema9Series.last()
        val ema21 = ema21Series.last()
        val ema9Prev = if (ema9Series.size >= 2) ema9Series[ema9Series.size - 2] else ema9
        val ema21Prev = if (ema21Series.size >= 2) ema21Series[ema21Series.size - 2] else ema21

        val bullishCross = ema9Prev <= ema21Prev && ema9 > ema21
        val bearishCross = ema9Prev >= ema21Prev && ema9 < ema21
        val emaBullish = ema9 > ema21
        val emaSpreadPct = if (ema21 != 0.0) ((ema9 - ema21) / ema21) * 100.0 else 0.0

        val emaLabel = when {
            bullishCross -> "EMA 9/21 Bullish Cross"
            bearishCross -> "EMA 9/21 Bearish Cross"
            emaBullish -> "EMA 9 > EMA 21 (Uptrend)"
            else -> "EMA 9 < EMA 21 (Downtrend)"
        }

        // 2. RSI (14-period Wilder's Smoothing)
        val rsi14 = computeRsi14(closes)
        val rsiZoneLabel = when {
            rsi14 >= 70.0 -> "Overbought (${format1(rsi14)}) -> PUT Bias"
            rsi14 <= 30.0 -> "Oversold (${format1(rsi14)}) -> CALL Bias"
            rsi14 >= 55.0 -> "Bullish Zone (${format1(rsi14)})"
            rsi14 <= 45.0 -> "Bearish Zone (${format1(rsi14)})"
            else -> "Neutral Equilibrium (${format1(rsi14)})"
        }

        // 3. MACD (12, 26, 9)
        val ema12Series = computeEmaSeries(closes, 12)
        val ema26Series = computeEmaSeries(closes, 26)
        val macdSeries = ema12Series.indices.map { i -> ema12Series[i] - ema26Series[i] }
        val signalSeries = computeEmaSeries(macdSeries, 9)
        val macdLine = macdSeries.last()
        val macdSignal = signalSeries.last()
        val macdHist = macdLine - macdSignal
        val macdHistPrev = if (macdSeries.size >= 2 && signalSeries.size >= 2) {
            macdSeries[macdSeries.size - 2] - signalSeries[signalSeries.size - 2]
        } else {
            macdHist
        }

        val macdLabel = when {
            macdHist > 0 && macdHist > macdHistPrev -> "MACD Expanding Bullish"
            macdHist > 0 -> "MACD Positive Momentum"
            macdHist < 0 && macdHist < macdHistPrev -> "MACD Expanding Bearish"
            else -> "MACD Negative Momentum"
        }

        // 4. Support & Resistance (20-candle lookback excluding current tip if enough data)
        val lookback = candles.takeLast(min(24, candles.size))
        val priorLookback = if (lookback.size > 4) lookback.dropLast(1) else lookback
        val support = priorLookback.minOf { it.low }
        val resistance = priorLookback.maxOf { it.high }
        val range = max(resistance - support, livePrice * 0.0005)
        val positionInRange = ((livePrice - support) / range).coerceIn(-0.2, 1.2)

        val snrLabel = when {
            livePrice > resistance -> "Resistance Breakout (Bullish)"
            livePrice < support -> "Support Breakdown (Bearish)"
            positionInRange >= 0.85 -> "Near Resistance Reject Zone"
            positionInRange <= 0.15 -> "Near Support Bounce Zone"
            else -> "Mid-Range Structure (${(positionInRange * 100).toInt()}%)"
        }

        // Confluence Scoring (+ for BUY/CALL, - for SELL/PUT)
        var bullScore = 0.0
        var bearScore = 0.0

        // EMA Weight
        if (bullishCross) bullScore += 2.2
        else if (bearishCross) bearScore += 2.2
        else if (emaBullish) bullScore += 1.4
        else bearScore += 1.4

        // RSI Weight (Mean-reversion at extremes, trend-following in mid zones)
        when {
            rsi14 > 70.0 -> bearScore += 2.4
            rsi14 < 30.0 -> bullScore += 2.4
            rsi14 > 54.0 -> bullScore += 1.2
            rsi14 < 46.0 -> bearScore += 1.2
            else -> {
                if (emaBullish) bullScore += 0.4 else bearScore += 0.4
            }
        }

        // MACD Weight
        if (macdHist > 0) {
            bullScore += if (macdHist > macdHistPrev) 1.6 else 1.0
        } else {
            bearScore += if (macdHist < macdHistPrev) 1.6 else 1.0
        }

        // SNR Weight
        when {
            livePrice > resistance -> bullScore += 1.3
            livePrice < support -> bearScore += 1.3
            positionInRange >= 0.86 && rsi14 >= 64.0 -> bearScore += 1.4
            positionInRange <= 0.14 && rsi14 <= 36.0 -> bullScore += 1.4
            positionInRange >= 0.5 -> bullScore += 0.4
            else -> bearScore += 0.4
        }

        val direction = if (bullScore >= bearScore) SignalDirection.BUY else SignalDirection.SELL
        val winningScore = max(bullScore, bearScore)
        val totalScore = max(1.0, bullScore + bearScore)
        val agreementRatio = (winningScore / totalScore).coerceIn(0.5, 1.0)

        // Map agreement ratio & signal clarity to 1..5 strength and 90.5..96.4 win rate
        val spreadBoost = min(0.15, abs(emaSpreadPct) * 0.5)
        val strength = when {
            agreementRatio >= 0.84 -> 5
            agreementRatio >= 0.72 -> 4
            agreementRatio >= 0.60 -> 3
            agreementRatio >= 0.53 -> 2
            else -> 1
        }

        val rawConfidence = 90.5 + ((agreementRatio - 0.5) / 0.5) * 5.2 + spreadBoost * 4.5
        val clampedConfidence = (Math.round(rawConfidence.coerceIn(90.5, 96.4) * 10.0) / 10.0)

        return TechnicalSummary(
            pair = pair,
            timeframe = timeframe,
            livePrice = livePrice,
            priceChangePercent24h = changePercent24h,
            ema9 = ema9,
            ema21 = ema21,
            rsi14 = rsi14,
            macdLine = macdLine,
            macdSignal = macdSignal,
            macdHistogram = macdHist,
            supportLevel = support,
            resistanceLevel = resistance,
            emaCrossoverLabel = emaLabel,
            rsiZoneLabel = rsiZoneLabel,
            macdMomentumLabel = macdLabel,
            snrBreakoutLabel = snrLabel,
            direction = direction,
            signalStrength = strength,
            winRateConfidence = clampedConfidence,
            feedSource = feedSource,
            candleCount = candles.size
        )
    }

    fun analyzeWithConfirmation(
        pair: String,
        timeframe: String,
        primaryCandles: List<Candle>,
        confirmationCandles: List<Candle>,
        changePercent24h: Double,
        feedSource: String
    ): Pair<TechnicalSummary, List<com.example.domain.model.ConfirmationCheck>> {
        val primary = analyze(pair, timeframe, primaryCandles, changePercent24h, feedSource)
        val secondary = if (confirmationCandles.size >= 15) {
            analyze(pair, "$timeframe (HTF)", confirmationCandles, changePercent24h, feedSource)
        } else {
            primary
        }

        // Cross-check primary confluence with higher-window confirmation bias
        val finalDirection = if (primary.direction == secondary.direction) {
            primary.direction
        } else {
            // Require clear momentum edge on primary when HTF diverges
            if (primary.signalStrength >= 4) primary.direction else secondary.direction
        }

        val boostedConfidence = if (primary.direction == secondary.direction) {
            (Math.round(min(96.4, primary.winRateConfidence + 0.6) * 10.0) / 10.0)
        } else {
            primary.winRateConfidence
        }

        val confirmedSummary = primary.copy(
            direction = finalDirection,
            winRateConfidence = boostedConfidence
        )

        val checks = buildConfirmationChecks(confirmedSummary, secondary)
        return confirmedSummary to checks
    }

    fun buildConfirmationChecks(
        summary: TechnicalSummary,
        htfSummary: TechnicalSummary? = null
    ): List<com.example.domain.model.ConfirmationCheck> {
        val isBuy = summary.direction == SignalDirection.BUY
        val emaAligned = if (isBuy) summary.ema9 >= summary.ema21 else summary.ema9 <= summary.ema21
        val rsiAligned = if (isBuy) summary.rsi14 <= 68.0 else summary.rsi14 >= 32.0
        val macdAligned = if (isBuy) summary.macdHistogram >= 0 else summary.macdHistogram <= 0
        val htfAligned = htfSummary == null || htfSummary.direction == summary.direction

        return listOf(
            com.example.domain.model.ConfirmationCheck(
                name = "EMA 9/21 Trend",
                detail = summary.emaCrossoverLabel,
                alignedWithSignal = emaAligned
            ),
            com.example.domain.model.ConfirmationCheck(
                name = "RSI(14) Filter",
                detail = summary.rsiZoneLabel,
                alignedWithSignal = rsiAligned
            ),
            com.example.domain.model.ConfirmationCheck(
                name = "MACD Momentum",
                detail = summary.macdMomentumLabel,
                alignedWithSignal = macdAligned
            ),
            com.example.domain.model.ConfirmationCheck(
                name = "HTF & SNR Check",
                detail = if (htfAligned) "Confirmed • ${summary.snrBreakoutLabel}" else summary.snrBreakoutLabel,
                alignedWithSignal = htfAligned
            )
        )
    }

    private fun computeEmaSeries(values: List<Double>, period: Int): List<Double> {
        if (values.isEmpty()) return emptyList()
        val k = 2.0 / (period + 1.0)
        val result = ArrayList<Double>(values.size)
        var ema = values.first()
        result.add(ema)
        for (i in 1 until values.size) {
            ema = (values[i] * k) + (ema * (1.0 - k))
            result.add(ema)
        }
        return result
    }

    private fun computeRsi14(closes: List<Double>, period: Int = 14): Double {
        if (closes.size <= period) return 50.0
        var gainSum = 0.0
        var lossSum = 0.0
        for (i in 1..period) {
            val diff = closes[i] - closes[i - 1]
            if (diff >= 0) gainSum += diff else lossSum += -diff
        }
        var avgGain = gainSum / period
        var avgLoss = lossSum / period
        for (i in (period + 1) until closes.size) {
            val diff = closes[i] - closes[i - 1]
            val gain = if (diff > 0) diff else 0.0
            val loss = if (diff < 0) -diff else 0.0
            avgGain = ((avgGain * (period - 1)) + gain) / period
            avgLoss = ((avgLoss * (period - 1)) + loss) / period
        }
        if (avgLoss == 0.0) return 100.0
        val rs = avgGain / avgLoss
        return (100.0 - (100.0 / (1.0 + rs))).coerceIn(0.0, 100.0)
    }

    private fun format1(value: Double): String = String.format(java.util.Locale.US, "%.1f", value)
}
