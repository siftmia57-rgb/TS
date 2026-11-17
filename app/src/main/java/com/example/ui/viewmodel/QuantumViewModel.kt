package com.example.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.PhotoChartAnalysis
import com.example.data.ai.QuantumAiRepository
import com.example.data.network.MarketDataRepository
import com.example.domain.model.BestSetupItem
import com.example.domain.model.MainBottomTab
import com.example.domain.model.MarketDirection
import com.example.domain.model.MarketQuoteItem
import com.example.domain.model.PaymentMethodTab
import com.example.domain.model.SignalDirection
import com.example.domain.model.TimeframeOption
import com.example.domain.model.TradingCatalog
import com.example.domain.model.TradingSignalResult
import com.example.domain.ta.TechnicalAnalysisEngine
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.random.Random

data class AnalyzingUiState(
    val pair: String = "",
    val timeframe: String = "",
    val progress: Float = 0f,
    val stageText: String = "Connecting to TradingView feed...!",
    val dataPoints: Int = 0,
    val accuracy: Double = 80.0,
    val confidence: Double = 78.0,
    val isComplete: Boolean = false
)

data class QuantumUiState(
    // Lock & Payment Gate
    val passwordInput: String = "",
    val isCheckingPassword: Boolean = false,
    val passwordError: String? = null,
    val isUnlocked: Boolean = false,
    val selectedPaymentTab: PaymentMethodTab = PaymentMethodTab.BINANCE_PAY,

    // Global Live Header & Clock
    val liveTradersCount: Int = 534,
    val entryCountdownSeconds: Int = 45,
    val nextCandleTimeFormatted: String = "00:00:00",
    val signalLifetimeSeconds: Int = 59,

    // Dashboard & Markets
    val activeBottomTab: MainBottomTab = MainBottomTab.TRADE,
    val bestSetups: List<BestSetupItem> = emptyList(),
    val isRefreshingSetups: Boolean = false,
    val marketQuotes: List<MarketQuoteItem> = emptyList(),
    val isLoadingMarkets: Boolean = false,
    val marketFilter: MarketDirection? = null,

    // Manual Analysis Flow
    val selectedDirection: MarketDirection = MarketDirection.OTC,
    val selectedPair: String = "EUR/USD OTC",
    val selectedTimeframe: TimeframeOption = TradingCatalog.otcTimeframes[4],

    // Photo Analysis Flow
    val selectedChartBitmap: Bitmap? = null,
    val isAnalyzingPhoto: Boolean = false,
    val photoAnalysisError: String? = null,

    // Analyzing Screen & Result
    val analyzingState: AnalyzingUiState = AnalyzingUiState(),
    val currentSignal: TradingSignalResult? = null,
    val pendingAnalyzingNavigation: Boolean = false,
    val pendingResultNavigationSignalId: String? = null
)

class QuantumViewModel(
    private val marketRepository: MarketDataRepository = MarketDataRepository(),
    private val aiRepository: QuantumAiRepository = QuantumAiRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuantumUiState())
    val uiState: StateFlow<QuantumUiState> = _uiState.asStateFlow()

    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.US)

    init {
        startLiveClockAndTelemetry()
        refreshBestSetups()
        refreshMarketQuotes()
    }

    private fun startLiveClockAndTelemetry() {
        viewModelScope.launch {
            var tickCounter = 0
            while (true) {
                val nowMs = System.currentTimeMillis()
                val secIntoMinute = ((nowMs / 1000L) % 60L).toInt()
                val remainingToNextMin = (60 - secIntoMinute).let { if (it == 0) 60 else it }
                val nextMinuteStartMs = ((nowMs / 60_000L) + 1L) * 60_000L
                val nextTimeStr = timeFormat.format(Date(nextMinuteStartMs))

                tickCounter++
                _uiState.update { state ->
                    val nextCount = if (tickCounter % 4 == 0) {
                        (state.liveTradersCount + Random.nextInt(-2, 4)).coerceIn(518, 568)
                    } else {
                        state.liveTradersCount
                    }
                    val nextLifetime = if (state.currentSignal != null && state.signalLifetimeSeconds > 0) {
                        state.signalLifetimeSeconds - 1
                    } else {
                        state.signalLifetimeSeconds
                    }
                    state.copy(
                        liveTradersCount = nextCount,
                        entryCountdownSeconds = remainingToNextMin,
                        nextCandleTimeFormatted = nextTimeStr,
                        signalLifetimeSeconds = nextLifetime
                    )
                }
                delay(1000L)
            }
        }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(passwordInput = value, passwordError = null) }
    }

    fun onPaymentTabSelected(tab: PaymentMethodTab) {
        _uiState.update { it.copy(selectedPaymentTab = tab) }
    }

    fun submitPassword() {
        if (_uiState.value.isCheckingPassword) return
        val entered = _uiState.value.passwordInput.trim()
        viewModelScope.launch {
            _uiState.update { it.copy(isCheckingPassword = true, passwordError = null) }
            delay(1500L)
            if (entered == "TS313579") {
                _uiState.update {
                    it.copy(
                        isCheckingPassword = false,
                        isUnlocked = true,
                        passwordError = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isCheckingPassword = false,
                        passwordError = "Invalid password! Please enter the valid TS Quantum access key."
                    )
                }
            }
        }
    }

    fun onBottomTabSelected(tab: MainBottomTab) {
        _uiState.update { it.copy(activeBottomTab = tab) }
        if (tab == MainBottomTab.MARKETS && _uiState.value.marketQuotes.isEmpty()) {
            refreshMarketQuotes()
        }
    }

    fun setMarketFilter(filter: MarketDirection?) {
        _uiState.update { it.copy(marketFilter = filter) }
    }

    fun refreshBestSetups() {
        if (_uiState.value.isRefreshingSetups) return
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshingSetups = true) }
            val nextMinuteStartMs = ((System.currentTimeMillis() / 60_000L) + 1L) * 60_000L
            val nextEntryTime = timeFormat.format(Date(nextMinuteStartMs))

            val setups = TradingCatalog.topScannerPairs.map { pair ->
                val feed = marketRepository.fetchCandles(pair, 60)
                val summary = TechnicalAnalysisEngine.analyze(
                    pair = pair,
                    timeframe = "1 MIN",
                    candles = feed.candles,
                    changePercent24h = feed.changePercent24h,
                    feedSource = feed.feedSource
                )
                BestSetupItem(
                    pair = pair,
                    timeframe = "1 MIN",
                    direction = summary.direction,
                    confidence = summary.winRateConfidence,
                    entryTimeFormatted = nextEntryTime,
                    rsi14 = summary.rsi14,
                    feedSource = feed.feedSource
                )
            }.sortedByDescending { it.confidence }

            _uiState.update {
                it.copy(
                    bestSetups = setups,
                    isRefreshingSetups = false
                )
            }
        }
    }

    fun refreshMarketQuotes() {
        if (_uiState.value.isLoadingMarkets) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMarkets = true) }
            val featuredPairs = listOf(
                "EUR/USD OTC" to MarketDirection.OTC,
                "NZD/JPY OTC" to MarketDirection.OTC,
                "USD/JPY OTC" to MarketDirection.OTC,
                "GBP/JPY OTC" to MarketDirection.OTC,
                "USD/TRY OTC" to MarketDirection.OTC,
                "USD/INR OTC" to MarketDirection.OTC,
                "BTC/USD OTC" to MarketDirection.OTC,
                "GOLD OTC" to MarketDirection.OTC,
                "EUR/USD" to MarketDirection.MARKET,
                "GBP/USD" to MarketDirection.MARKET,
                "USD/JPY" to MarketDirection.MARKET,
                "GOLD" to MarketDirection.MARKET,
                "APPLE" to MarketDirection.MARKET,
                "TESLA" to MarketDirection.MARKET
            )
            val quotes = featuredPairs.map { (pair, category) ->
                val feed = marketRepository.fetchCandles(pair, 60)
                val summary = TechnicalAnalysisEngine.analyze(
                    pair = pair,
                    timeframe = "1 MIN",
                    candles = feed.candles,
                    changePercent24h = feed.changePercent24h,
                    feedSource = feed.feedSource
                )
                MarketQuoteItem(
                    pair = pair,
                    category = category,
                    price = summary.livePrice,
                    changePercent = summary.priceChangePercent24h,
                    direction = summary.direction,
                    rsi14 = summary.rsi14,
                    emaTrend = summary.emaCrossoverLabel,
                    feedSource = feed.feedSource
                )
            }
            _uiState.update {
                it.copy(
                    marketQuotes = quotes,
                    isLoadingMarkets = false
                )
            }
        }
    }

    fun selectDirection(direction: MarketDirection) {
        _uiState.update { it.copy(selectedDirection = direction) }
    }

    fun selectPair(pair: String) {
        val defaultTf = if (pair.endsWith("OTC", ignoreCase = true)) {
            TradingCatalog.otcTimeframes[4]
        } else {
            TradingCatalog.marketTimeframes[0]
        }
        _uiState.update {
            it.copy(
                selectedPair = pair,
                selectedDirection = if (pair.endsWith("OTC", ignoreCase = true)) {
                    MarketDirection.OTC
                } else {
                    MarketDirection.MARKET
                },
                selectedTimeframe = defaultTf
            )
        }
    }

    fun selectTimeframeAndAnalyze(timeframe: TimeframeOption) {
        _uiState.update { it.copy(selectedTimeframe = timeframe) }
        runManualLiveAnalysis(
            pair = _uiState.value.selectedPair,
            timeframe = timeframe
        )
    }

    fun runQuickSetupAnalysis(pair: String, timeframeLabel: String) {
        val isOtc = pair.endsWith("OTC", ignoreCase = true)
        val pool = if (isOtc) TradingCatalog.otcTimeframes else TradingCatalog.marketTimeframes
        val tf = pool.firstOrNull { it.label == timeframeLabel } ?: pool.first()
        _uiState.update {
            it.copy(
                selectedDirection = if (isOtc) MarketDirection.OTC else MarketDirection.MARKET,
                selectedPair = pair,
                selectedTimeframe = tf
            )
        }
        runManualLiveAnalysis(pair, tf)
    }

    fun regenerateCurrentSignal() {
        val current = _uiState.value.currentSignal ?: return
        val isOtc = current.pair.endsWith("OTC", ignoreCase = true)
        val pool = if (isOtc) TradingCatalog.otcTimeframes else TradingCatalog.marketTimeframes
        val tf = pool.firstOrNull { it.label == current.timeframe }
            ?: TimeframeOption(current.timeframe, "Live re-scan", 60)
        runManualLiveAnalysis(current.pair, tf)
    }

    private fun runManualLiveAnalysis(pair: String, timeframe: TimeframeOption) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    pendingResultNavigationSignalId = null,
                    analyzingState = AnalyzingUiState(
                        pair = pair,
                        timeframe = timeframe.label,
                        progress = 0.02f,
                        stageText = "Connecting to TradingView feed...!",
                        dataPoints = 120,
                        accuracy = 81.4,
                        confidence = 79.2,
                        isComplete = false
                    )
                )
            }

            // Launch real-time market fetch + HTF confirmation + TA computation + Gemini AI reasoning in parallel
            val analysisDeferred = async {
                val feed = marketRepository.fetchCandles(pair, timeframe.seconds)
                val confirmationIntervalSec = if (timeframe.seconds < 60) 60 else timeframe.seconds * 2
                val htfFeed = marketRepository.fetchCandles(pair, confirmationIntervalSec)

                val (techSummary, baseChecks) = TechnicalAnalysisEngine.analyzeWithConfirmation(
                    pair = pair,
                    timeframe = timeframe.label,
                    primaryCandles = feed.candles,
                    confirmationCandles = htfFeed.candles,
                    changePercent24h = feed.changePercent24h,
                    feedSource = feed.feedSource
                )
                val aiResult = aiRepository.refineSignalWithGemini(techSummary)
                val aiRefinement = aiResult.getOrNull()
                val aiNotice = aiResult.exceptionOrNull()?.let { err ->
                    "Gemini AI offline/unavailable (${err.localizedMessage ?: "App Check/Network"}). Showing live technical indicator engine output."
                }

                val finalDir = aiRefinement?.direction ?: techSummary.direction
                val aiCheck = com.example.domain.model.ConfirmationCheck(
                    name = "Gemini AI Confirm",
                    detail = if (aiRefinement != null) {
                        "Verified ${if (finalDir == SignalDirection.BUY) "BUY (LONG)" else "SELL (SHORT)"}"
                    } else {
                        "Quant Engine Fallback"
                    },
                    alignedWithSignal = aiRefinement != null
                )
                val allChecks = baseChecks + aiCheck
                val confirmedCount = allChecks.count { it.alignedWithSignal }

                TradingSignalResult(
                    id = UUID.randomUUID().toString(),
                    pair = pair,
                    timeframe = timeframe.label,
                    direction = finalDir,
                    signalStrength = aiRefinement?.signalStrength ?: techSummary.signalStrength,
                    winRateConfidence = aiRefinement?.winRateConfidence ?: techSummary.winRateConfidence,
                    livePrice = techSummary.livePrice,
                    emaStatus = techSummary.emaCrossoverLabel,
                    rsiValue = techSummary.rsiZoneLabel,
                    macdStatus = techSummary.macdMomentumLabel,
                    snrStatus = techSummary.snrBreakoutLabel,
                    aiReasoning = aiRefinement?.reasoning
                        ?: "${techSummary.emaCrossoverLabel}. ${techSummary.rsiZoneLabel}. ${techSummary.macdMomentumLabel}.",
                    aiStatusNotice = aiNotice,
                    isFromPhoto = false,
                    feedSource = techSummary.feedSource,
                    createdAtMillis = System.currentTimeMillis(),
                    confirmationChecks = allChecks,
                    confirmationScoreLabel = "$confirmedCount/${allChecks.size} CONFIRMED"
                )
            }

            runFourSecondProgressAnimation(pair, timeframe.label)

            val signal = analysisDeferred.await()
            _uiState.update {
                it.copy(
                    analyzingState = it.analyzingState.copy(
                        progress = 1f,
                        dataPoints = 6130,
                        accuracy = 95.0,
                        confidence = signal.winRateConfidence,
                        isComplete = true
                    ),
                    currentSignal = signal,
                    signalLifetimeSeconds = 59,
                    pendingResultNavigationSignalId = signal.id
                )
            }
        }
    }

    fun onChartBitmapSelected(bitmap: Bitmap) {
        _uiState.update {
            it.copy(
                selectedChartBitmap = bitmap,
                photoAnalysisError = null
            )
        }
    }

    fun analyzeSelectedChartPhoto() {
        val bitmap = _uiState.value.selectedChartBitmap ?: return
        if (_uiState.value.isAnalyzingPhoto) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isAnalyzingPhoto = true,
                    photoAnalysisError = null
                )
            }
            val result = aiRepository.analyzeChartScreenshot(bitmap)
            result.fold(
                onSuccess = { photoAnalysis ->
                    _uiState.update {
                        it.copy(
                            isAnalyzingPhoto = false,
                            photoAnalysisError = null
                        )
                    }
                    launchPhotoAnalyzingSequence(photoAnalysis)
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isAnalyzingPhoto = false,
                            photoAnalysisError = error.localizedMessage
                                ?: "Failed to analyze chart screenshot with Gemini Vision."
                        )
                    }
                }
            )
        }
    }

    private fun launchPhotoAnalyzingSequence(analysis: PhotoChartAnalysis) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    pendingAnalyzingNavigation = true,
                    pendingResultNavigationSignalId = null,
                    analyzingState = AnalyzingUiState(
                        pair = analysis.detectedPair,
                        timeframe = analysis.detectedTimeframe,
                        progress = 0.05f,
                        stageText = "Connecting to TradingView feed...!",
                        dataPoints = 240,
                        accuracy = 82.0,
                        confidence = 80.0,
                        isComplete = false
                    )
                )
            }

            runFourSecondProgressAnimation(analysis.detectedPair, analysis.detectedTimeframe)

            val signal = TradingSignalResult(
                id = UUID.randomUUID().toString(),
                pair = analysis.detectedPair,
                timeframe = analysis.detectedTimeframe,
                direction = analysis.direction,
                signalStrength = analysis.signalStrength,
                winRateConfidence = analysis.winRateConfidence,
                livePrice = null,
                emaStatus = analysis.candlestickTrend,
                rsiValue = "Vision Candlestick Pattern",
                macdStatus = "AI Chart Momentum Read",
                snrStatus = analysis.snrFvgZones,
                aiReasoning = analysis.reasoning,
                aiStatusNotice = null,
                isFromPhoto = true,
                feedSource = "Gemini Vision Chart Scanner",
                createdAtMillis = System.currentTimeMillis()
            )

            _uiState.update {
                it.copy(
                    analyzingState = it.analyzingState.copy(
                        progress = 1f,
                        dataPoints = 6130,
                        accuracy = 95.0,
                        confidence = signal.winRateConfidence,
                        isComplete = true
                    ),
                    currentSignal = signal,
                    signalLifetimeSeconds = 59,
                    pendingResultNavigationSignalId = signal.id
                )
            }
        }
    }

    fun consumeAnalyzingNavigation() {
        _uiState.update { it.copy(pendingAnalyzingNavigation = false) }
    }

    fun consumeResultNavigation() {
        _uiState.update { it.copy(pendingResultNavigationSignalId = null) }
    }

    private suspend fun runFourSecondProgressAnimation(pair: String, timeframe: String) {
        val steps = 40
        val stepDelayMs = 100L // 40 * 100ms = 4000ms (4-second animated sequence)
        for (step in 1..steps) {
            val fraction = step / steps.toFloat()
            val stage = when {
                fraction < 0.25f -> "Connecting to TradingView feed...!"
                fraction < 0.55f -> "Analyzing volatility & RSI/EMA...!"
                fraction < 0.85f -> "Running neural forecast...!"
                else -> "Confirming signal...!"
            }
            val dataPoints = (120 + (6010 * fraction)).toInt()
            val accuracy = Math.round((81.4 + (13.6 * fraction)) * 10.0) / 10.0
            val confidence = Math.round((79.2 + (14.8 * fraction)) * 10.0) / 10.0

            _uiState.update {
                it.copy(
                    analyzingState = AnalyzingUiState(
                        pair = pair,
                        timeframe = timeframe,
                        progress = fraction,
                        stageText = stage,
                        dataPoints = dataPoints,
                        accuracy = accuracy,
                        confidence = confidence,
                        isComplete = false
                    )
                )
            }
            delay(stepDelayMs)
        }
    }
}
