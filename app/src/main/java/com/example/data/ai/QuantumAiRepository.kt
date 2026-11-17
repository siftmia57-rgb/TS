package com.example.data.ai

import android.graphics.Bitmap
import android.util.Log
import com.example.domain.model.SignalDirection
import com.example.domain.model.TechnicalSummary
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

private const val TAG = "QuantumAiRepository"

data class AiSignalRefinement(
    val direction: SignalDirection,
    val signalStrength: Int,
    val winRateConfidence: Double,
    val reasoning: String
)

data class PhotoChartAnalysis(
    val detectedPair: String,
    val detectedTimeframe: String,
    val direction: SignalDirection,
    val signalStrength: Int,
    val winRateConfidence: Double,
    val candlestickTrend: String,
    val snrFvgZones: String,
    val reasoning: String
)

class QuantumAiRepository {

    private val signalSchema = Schema.obj(
        properties = mapOf(
            "direction" to Schema.enumeration(
                values = listOf("BUY", "SELL"),
                description = "Forecast trade direction"
            ),
            "signalStrength" to Schema.integer(
                description = "Signal strength integer from 1 to 5"
            ),
            "winRateConfidence" to Schema.double(
                description = "Confidence percentage between 90.5 and 96.4"
            ),
            "reasoning" to Schema.string(
                description = "Concise 1-2 sentence technical analysis rationale referencing EMA, RSI, MACD, and SNR"
            )
        )
    )

    private val photoSchema = Schema.obj(
        properties = mapOf(
            "detectedPair" to Schema.string(
                description = "Detected trading pair or asset from the chart screenshot, e.g. EUR/USD OTC"
            ),
            "detectedTimeframe" to Schema.string(
                description = "Detected chart timeframe such as 1 MIN, 5 MIN, or 30 SEC"
            ),
            "direction" to Schema.enumeration(
                values = listOf("BUY", "SELL"),
                description = "Forecast trade direction"
            ),
            "signalStrength" to Schema.integer(
                description = "Signal strength integer from 1 to 5"
            ),
            "winRateConfidence" to Schema.double(
                description = "Confidence percentage between 90.5 and 96.4"
            ),
            "candlestickTrend" to Schema.string(
                description = "Detected candlestick momentum and trend structure"
            ),
            "snrFvgZones" to Schema.string(
                description = "Detected Support/Resistance (SNR) or Fair Value Gap (FVG) zones"
            ),
            "reasoning" to Schema.string(
                description = "Concise explanation of the chart setup and entry signal"
            )
        )
    )

    private val signalModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = signalSchema
                temperature = 0.25f
            }
        )
    }

    private val visionModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = photoSchema
                temperature = 0.2f
            }
        )
    }

    suspend fun refineSignalWithGemini(summary: TechnicalSummary): Result<AiSignalRefinement> {
        return try {
            val prompt = buildString {
                appendLine("You are the TS Quantum quantitative trading analysis engine.")
                appendLine("Evaluate these live technical indicators for ${summary.pair} (${summary.timeframe}):")
                appendLine("- Live Price: ${summary.livePrice}")
                appendLine("- EMA 9: ${summary.ema9} | EMA 21: ${summary.ema21} -> ${summary.emaCrossoverLabel}")
                appendLine("- RSI (14): ${summary.rsi14} -> ${summary.rsiZoneLabel}")
                appendLine("- MACD Line: ${summary.macdLine}, Signal: ${summary.macdSignal}, Hist: ${summary.macdHistogram} -> ${summary.macdMomentumLabel}")
                appendLine("- Support: ${summary.supportLevel}, Resistance: ${summary.resistanceLevel} -> ${summary.snrBreakoutLabel}")
                appendLine("- Baseline Confluence: ${summary.direction.name}, Strength: ${summary.signalStrength}/5, Confidence: ${summary.winRateConfidence}%")
                appendLine("Confirm or refine BUY vs SELL, strength (1..5), confidence (90.5..96.4), and provide a sharp 1-2 sentence technical rationale.")
            }

            val response = withTimeout(30_000L) {
                signalModel.generateContent(prompt)
            }
            val raw = response.text ?: throw IllegalStateException("Gemini returned empty response.")
            val json = JSONObject(raw)
            val dir = if (json.optString("direction", summary.direction.name).uppercase() == "SELL") {
                SignalDirection.SELL
            } else {
                SignalDirection.BUY
            }
            val strength = json.optInt("signalStrength", summary.signalStrength).coerceIn(1, 5)
            val conf = json.optDouble("winRateConfidence", summary.winRateConfidence).coerceIn(90.5, 96.4)
            val reasoning = json.optString(
                "reasoning",
                "${summary.emaCrossoverLabel}; ${summary.rsiZoneLabel}; ${summary.macdMomentumLabel}."
            )
            Result.success(
                AiSignalRefinement(
                    direction = dir,
                    signalStrength = strength,
                    winRateConfidence = Math.round(conf * 10.0) / 10.0,
                    reasoning = reasoning
                )
            )
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Gemini signal refinement timed out", e)
            Result.failure(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Gemini signal refinement failed", e)
            Result.failure(e)
        }
    }

    suspend fun analyzeChartScreenshot(bitmap: Bitmap): Result<PhotoChartAnalysis> {
        return try {
            val scaledBitmap = scaleBitmapUnderLimit(bitmap)
            val response = withTimeout(60_000L) {
                visionModel.generateContent(
                    content {
                        image(scaledBitmap)
                        text(
                            "Analyze this Quotex / TradingView trading chart screenshot. " +
                                "Identify the currency/asset pair shown (e.g. EUR/USD OTC), the chart timeframe (e.g. 1 MIN), " +
                                "candlestick trend structure, Support/Resistance (SNR) and Fair Value Gap (FVG) zones, " +
                                "and output a BUY or SELL forecast with strength (1-5) and confidence (90.5-96.4)."
                        )
                    }
                )
            }
            val raw = response.text ?: throw IllegalStateException("Gemini Vision returned empty text.")
            val json = JSONObject(raw)
            val dir = if (json.optString("direction", "BUY").uppercase() == "SELL") {
                SignalDirection.SELL
            } else {
                SignalDirection.BUY
            }
            Result.success(
                PhotoChartAnalysis(
                    detectedPair = json.optString("detectedPair", "EUR/USD OTC").ifBlank { "EUR/USD OTC" },
                    detectedTimeframe = json.optString("detectedTimeframe", "1 MIN").ifBlank { "1 MIN" },
                    direction = dir,
                    signalStrength = json.optInt("signalStrength", 4).coerceIn(1, 5),
                    winRateConfidence = Math.round(
                        json.optDouble("winRateConfidence", 94.1).coerceIn(90.5, 96.4) * 10.0
                    ) / 10.0,
                    candlestickTrend = json.optString("candlestickTrend", "Impulsive price structure detected"),
                    snrFvgZones = json.optString("snrFvgZones", "Key SNR / FVG liquidity reaction zone"),
                    reasoning = json.optString("reasoning", "Chart structure & candle momentum confirm entry.")
                )
            )
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Gemini Vision chart analysis timed out", e)
            Result.failure(IllegalStateException("Vision analysis timed out after 60 seconds. Please try again."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Gemini Vision chart analysis failed", e)
            Result.failure(e)
        }
    }

    private fun scaleBitmapUnderLimit(bitmap: Bitmap, maxDim: Int = 1440): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxDim && height <= maxDim) return bitmap
        val ratio = minOf(maxDim.toFloat() / width, maxDim.toFloat() / height)
        val newW = (width * ratio).toInt().coerceAtLeast(1)
        val newH = (height * ratio).toInt().coerceAtLeast(1)
        return Bitmap.createScaledBitmap(bitmap, newW, newH, true)
    }
}
