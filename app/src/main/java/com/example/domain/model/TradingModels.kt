package com.example.domain.model

enum class MarketDirection(val title: String) {
    OTC("OTC PAIRS"),
    MARKET("MARKET PAIRS")
}

enum class SignalDirection {
    BUY,
    SELL
}

enum class PaymentMethodTab {
    BINANCE_PAY,
    NAGAD
}

enum class MainBottomTab {
    TRADE,
    MARKETS
}

data class Candle(
    val timestampMs: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Double
)

data class TimeframeOption(
    val label: String,
    val subtitle: String,
    val seconds: Int
)

data class TechnicalSummary(
    val pair: String,
    val timeframe: String,
    val livePrice: Double,
    val priceChangePercent24h: Double,
    val ema9: Double,
    val ema21: Double,
    val rsi14: Double,
    val macdLine: Double,
    val macdSignal: Double,
    val macdHistogram: Double,
    val supportLevel: Double,
    val resistanceLevel: Double,
    val emaCrossoverLabel: String,
    val rsiZoneLabel: String,
    val macdMomentumLabel: String,
    val snrBreakoutLabel: String,
    val direction: SignalDirection,
    val signalStrength: Int,
    val winRateConfidence: Double,
    val feedSource: String,
    val candleCount: Int
)

data class BestSetupItem(
    val pair: String,
    val timeframe: String,
    val direction: SignalDirection,
    val confidence: Double,
    val entryTimeFormatted: String,
    val rsi14: Double,
    val feedSource: String
)

data class MarketQuoteItem(
    val pair: String,
    val category: MarketDirection,
    val price: Double,
    val changePercent: Double,
    val direction: SignalDirection,
    val rsi14: Double,
    val emaTrend: String,
    val feedSource: String
)

data class ConfirmationCheck(
    val name: String,
    val detail: String,
    val alignedWithSignal: Boolean
)

data class TradingSignalResult(
    val id: String,
    val pair: String,
    val timeframe: String,
    val direction: SignalDirection,
    val signalStrength: Int,
    val winRateConfidence: Double,
    val livePrice: Double?,
    val emaStatus: String,
    val rsiValue: String,
    val macdStatus: String,
    val snrStatus: String,
    val aiReasoning: String,
    val aiStatusNotice: String?,
    val isFromPhoto: Boolean,
    val feedSource: String,
    val createdAtMillis: Long,
    val confirmationChecks: List<ConfirmationCheck> = emptyList(),
    val confirmationScoreLabel: String = ""
)

object TradingCatalog {
    val otcPairs: List<String> = listOf(
        "USD/INR OTC", "USD/TRY OTC", "EUR/USD OTC", "NZD/JPY OTC", "USD/JPY OTC",
        "GBP/JPY OTC", "AUD/USD OTC", "USD/CAD OTC", "EUR/GBP OTC", "AUD/JPY OTC",
        "CAD/JPY OTC", "CHF/JPY OTC", "EUR/CHF OTC", "EUR/AUD OTC", "EUR/CAD OTC",
        "GBP/AUD OTC", "GBP/CAD OTC", "GBP/CHF OTC", "NZD/CAD OTC", "NZD/USD OTC",
        "AUD/CHF OTC", "AUD/NZD OTC", "USD/BRL OTC", "USD/BDT OTC", "USD/PKR OTC",
        "USD/EGP OTC", "USD/ARS OTC", "BTC/USD OTC", "ETH/USD OTC", "GOLD OTC"
    )

    val marketPairs: List<String> = listOf(
        "EUR/USD", "GBP/USD", "USD/JPY", "AUD/USD", "USD/CAD",
        "USD/CHF", "NZD/USD", "EUR/GBP", "EUR/JPY", "GBP/JPY",
        "AUD/JPY", "EUR/AUD", "EUR/CAD", "GBP/CAD", "AUD/CAD",
        "CHF/JPY", "GOLD", "SILVER", "US CRUDE", "APPLE",
        "MICROSOFT", "TESLA", "AMAZON", "NETFLIX"
    )

    val otcTimeframes: List<TimeframeOption> = listOf(
        TimeframeOption("5 SEC", "High-frequency AI signals", 5),
        TimeframeOption("10 SEC", "Rapid market analysis", 10),
        TimeframeOption("15 SEC", "Enhanced precision mode", 15),
        TimeframeOption("30 SEC", "Deep learning analysis", 30),
        TimeframeOption("1 MIN", "Balanced trend read", 60),
        TimeframeOption("2 MIN", "Momentum confirmation", 120),
        TimeframeOption("3 MIN", "Multi-candle structure", 180),
        TimeframeOption("5 MIN", "Swing precision mode", 300)
    )

    val marketTimeframes: List<TimeframeOption> = listOf(
        TimeframeOption("1 MIN", "Balanced trend read", 60),
        TimeframeOption("2 MIN", "Momentum confirmation", 120),
        TimeframeOption("3 MIN", "Multi-candle structure", 180),
        TimeframeOption("4 MIN", "Trend continuation read", 240),
        TimeframeOption("5 MIN", "Swing precision mode", 300)
    )

    val topScannerPairs: List<String> = listOf(
        "NZD/JPY OTC",
        "USD/JPY OTC",
        "EUR/USD OTC",
        "USD/TRY OTC",
        "GBP/JPY OTC"
    )
}
