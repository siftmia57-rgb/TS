package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.components.QuantumAiBrainGlyph
import com.example.ui.components.QuantumBackground
import com.example.ui.components.StickyTopHeader
import com.example.ui.theme.NeonRed
import com.example.ui.theme.SignalGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AnalyzingUiState
import java.util.Locale

@Composable
fun AnalyzingScreen(
    liveTradersCount: Int,
    analyzingState: AnalyzingUiState
) {
    // Keep user on analyzing screen during the 4-second sequence
    BackHandler(enabled = true) { }

    val infiniteTransition = rememberInfiniteTransition(label = "analyzingRing")
    val sweepRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweepRotation"
    )

    QuantumBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            StickyTopHeader(liveUsersCount = liveTradersCount)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.widthIn(max = 560.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Quotex Broker Badge Header
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0x802D0A0A)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.BarChart,
                                contentDescription = "Quotex Broker",
                                tint = NeonRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Quotex Broker",
                                color = TextPrimary,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "SIGNALS OPTIMIZED ONLY",
                                color = NeonRed,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    Text(
                        text = "Confirmation Analysis... ${analyzingState.pair}",
                        color = TextPrimary,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Timeframe: ${analyzingState.timeframe}",
                        color = NeonRed,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Center Circular Progress Ring + Glowing AI Brain Icon
                Box(
                    modifier = Modifier.size(185.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Track circle
                        drawCircle(
                            color = Color(0x33FF2A2A),
                            style = Stroke(width = 12f)
                        )
                        // Progress sweep arc
                        drawArc(
                            color = NeonRed,
                            startAngle = -90f,
                            sweepAngle = analyzingState.progress * 360f,
                            useCenter = false,
                            style = Stroke(width = 14f, cap = StrokeCap.Round)
                        )
                        // Rotating radar orbital arc
                        rotate(sweepRotation) {
                            drawArc(
                                color = Color(0xAAFF6B6B),
                                startAngle = 0f,
                                sweepAngle = 65f,
                                useCenter = false,
                                style = Stroke(width = 6f, cap = StrokeCap.Round)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(126.dp)
                            .clip(RoundedCornerShape(63.dp))
                            .background(Color(0x552D0A0A)),
                        contentAlignment = Alignment.Center
                    ) {
                        QuantumAiBrainGlyph(
                            modifier = Modifier.size(76.dp),
                            color = NeonRed
                        )
                    }
                }

                // Live Confirmation Checklist + Stage Progress Bar + 3 Metric Cards
                Column(
                    modifier = Modifier.widthIn(max = 560.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GlassCard(contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)) {
                        ConfirmationProgressRow("1. TradingView Live Feed", analyzingState.progress >= 0.22f)
                        ConfirmationProgressRow("2. EMA 9/21 & RSI(14) Confluence", analyzingState.progress >= 0.48f)
                        ConfirmationProgressRow("3. MACD & HTF SNR Confirmation", analyzingState.progress >= 0.75f)
                        ConfirmationProgressRow("4. Gemini AI Signal Verification", analyzingState.progress >= 0.94f)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = analyzingState.stageText,
                        color = TextPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { analyzingState.progress.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(50)),
                        color = NeonRed,
                        trackColor = Color(0x40FF2A2A)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricTickCard(
                            label = "DATA POINTS",
                            value = String.format(Locale.US, "%,d", analyzingState.dataPoints),
                            valueColor = TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        MetricTickCard(
                            label = "ACCURACY",
                            value = String.format(Locale.US, "%.1f%%", analyzingState.accuracy),
                            valueColor = SignalGreen,
                            modifier = Modifier.weight(1f)
                        )
                        MetricTickCard(
                            label = "CONFIDENCE",
                            value = String.format(Locale.US, "%.0f%%", analyzingState.confidence),
                            valueColor = NeonRed,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConfirmationProgressRow(label: String, confirmed: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = if (confirmed) TextPrimary else TextMuted,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (confirmed) FontWeight.SemiBold else FontWeight.Normal
        )
        Text(
            text = if (confirmed) "✓ CONFIRMED" else "SCANNING...",
            color = if (confirmed) SignalGreen else NeonRed,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun MetricTickCard(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp)
    ) {
        Text(
            text = value,
            color = valueColor,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = TextMuted,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            letterSpacing = 0.4.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
