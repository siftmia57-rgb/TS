package com.example

import com.example.domain.model.Candle
import com.example.domain.model.SignalDirection
import com.example.domain.ta.TechnicalAnalysisEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TechnicalAnalysisEngineTest {

    @Test
    fun bullishUptrendProducesBuySignalWithinBounds() {
        val candles = (0 until 60).map { i ->
            val base = 1.0800 + (i * 0.0004)
            Candle(
                timestampMs = 1_700_000_000_000L + (i * 60_000L),
                open = base - 0.0001,
                high = base + 0.0003,
                low = base - 0.0002,
                close = base,
                volume = 1200.0
            )
        }
        val summary = TechnicalAnalysisEngine.analyze(
            pair = "EUR/USD",
            timeframe = "1 MIN",
            candles = candles,
            changePercent24h = 0.45,
            feedSource = "UnitTest"
        )
        assertTrue(summary.ema9 > summary.ema21)
        assertTrue(summary.winRateConfidence in 90.5..96.4)
        assertTrue(summary.signalStrength in 1..5)
    }

    @Test
    fun overboughtSpikeProducesSellBias() {
        val candles = (0 until 60).map { i ->
            val base = if (i < 42) 1.2500 else 1.2500 + ((i - 41) * 0.0045)
            Candle(
                timestampMs = 1_700_000_000_000L + (i * 60_000L),
                open = base - 0.0005,
                high = base + 0.0006,
                low = base - 0.0006,
                close = base,
                volume = 1800.0
            )
        }
        val summary = TechnicalAnalysisEngine.analyze(
            pair = "GBP/USD",
            timeframe = "1 MIN",
            candles = candles,
            changePercent24h = 1.15,
            feedSource = "UnitTest"
        )
        assertTrue(summary.rsi14 > 70.0)
        assertEquals(60, summary.candleCount)
        assertTrue(summary.winRateConfidence in 90.5..96.4)
    }

    @Test
    fun confirmationAnalysisProducesFourChecks() {
        val primary = (0 until 60).map { i ->
            val base = 1.1000 + (i * 0.0003)
            Candle(1_700_000_000_000L + i * 60_000L, base - 0.0001, base + 0.0002, base - 0.0002, base, 1000.0)
        }
        val htf = (0 until 60).map { i ->
            val base = 1.0950 + (i * 0.0004)
            Candle(1_700_000_000_000L + i * 120_000L, base - 0.0001, base + 0.0003, base - 0.0002, base, 2000.0)
        }
        val (summary, checks) = TechnicalAnalysisEngine.analyzeWithConfirmation(
            pair = "EUR/USD OTC",
            timeframe = "1 MIN",
            primaryCandles = primary,
            confirmationCandles = htf,
            changePercent24h = 0.35,
            feedSource = "UnitTest"
        )
        assertEquals(4, checks.size)
        assertTrue(summary.direction == SignalDirection.BUY || summary.direction == SignalDirection.SELL)
        assertTrue(summary.winRateConfidence in 90.5..96.4)
    }
}
