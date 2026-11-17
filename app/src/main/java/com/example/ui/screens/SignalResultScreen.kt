package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.SignalDirection
import com.example.domain.model.TradingSignalResult
import com.example.ui.components.GlassCard
import com.example.ui.components.QuantumBackground
import com.example.ui.components.QuantumGlowingButton
import com.example.ui.components.QuantumOutlineButton
import com.example.ui.components.StickyTopHeader
import com.example.ui.theme.NeonRed
import com.example.ui.theme.SignalGreen
import com.example.ui.theme.SignalGreenBg
import com.example.ui.theme.SignalGreenGlow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun SignalResultScreen(
    liveTradersCount: Int,
    signal: TradingSignalResult,
    onNewSignal: () -> Unit,
    onChangePair: () -> Unit
) {
    BackHandler(onBack = onChangePair)

    val isBuy = signal.direction == SignalDirection.BUY
    val accent = if (isBuy) SignalGreen else NeonRed
    val accentBg = if (isBuy) SignalGreenBg else Color(0x33FF2A2A)
    val glowColor = if (isBuy) SignalGreenGlow else NeonRed

    val infiniteTransition = rememberInfiniteTransition(label = "resultGlow")
    val ringAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ringAlpha"
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
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.widthIn(max = 580.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header: Quotex Broker / SIGNALS OPTIMIZED ONLY
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

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Signal for: ${signal.pair}  |  Timeframe: ${signal.timeframe}",
                        color = TextSecondary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Center Glowing Circle + Direction Badge + Big BUY/SELL Text
                    Box(
                        modifier = Modifier
                            .size(126.dp)
                            .shadow(
                                elevation = 24.dp,
                                shape = CircleShape,
                                ambientColor = glowColor,
                                spotColor = glowColor
                            )
                            .clip(CircleShape)
                            .background(accentBg)
                            .border(3.dp, accent.copy(alpha = ringAlpha), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isBuy) Icons.Filled.TrendingUp else Icons.Filled.TrendingDown,
                            contentDescription = if (isBuy) "Upward trend" else "Downward trend",
                            tint = accent,
                            modifier = Modifier.size(62.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = accentBg,
                        border = BorderStroke(1.dp, accent)
                    ) {
                        Text(
                            text = if (isBuy) "NEXT CANDLE • UPWARD" else "NEXT CANDLE • DOWNWARD",
                            color = accent,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.2.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isBuy) "BUY (LONG)" else "SELL (SHORT)",
                        color = accent,
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.testTag("signal_direction_text")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Two Metric Cards: SIGNAL STRENGTH & WIN RATE
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        GlassCard(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(14.dp)
                        ) {
                            Text(
                                text = "SIGNAL STRENGTH",
                                color = TextMuted,
                                style = MaterialTheme.typography.labelSmall,
                                letterSpacing = 0.6.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                for (i in 1..5) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (i <= signal.signalStrength) accent else Color(0x33FFFFFF)
                                            )
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${signal.signalStrength}/5",
                                    color = TextPrimary,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        GlassCard(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(14.dp)
                        ) {
                            Text(
                                text = "WIN RATE",
                                color = TextMuted,
                                style = MaterialTheme.typography.labelSmall,
                                letterSpacing = 0.6.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = String.format(Locale.US, "%.1f%%", signal.winRateConfidence),
                                    color = SignalGreen,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "Confirmed",
                                    color = SignalGreen,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Live Technical Confluence & Gemini AI Confirmation Card
                    GlassCard(contentPadding = PaddingValues(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${signal.feedSource.uppercase()}",
                                color = NeonRed,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            if (signal.confirmationScoreLabel.isNotBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = SignalGreenBg,
                                    border = BorderStroke(1.dp, SignalGreen.copy(alpha = 0.7f))
                                ) {
                                    Text(
                                        text = signal.confirmationScoreLabel,
                                        color = SignalGreen,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        if (signal.livePrice != null) {
                            Text(
                                text = "Spot Price: ${formatPrice(signal.livePrice)}",
                                color = TextPrimary,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        if (signal.confirmationChecks.isNotEmpty()) {
                            signal.confirmationChecks.forEach { check ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${if (check.alignedWithSignal) "✓" else "•"} ${check.name}",
                                        color = if (check.alignedWithSignal) SignalGreen else TextSecondary,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = check.detail,
                                        color = TextSecondary,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = "${signal.emaStatus} • ${signal.rsiValue}",
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "${signal.macdStatus} • ${signal.snrStatus}",
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = signal.aiReasoning,
                            color = TextPrimary,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                        if (signal.aiStatusNotice != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = signal.aiStatusNotice,
                                color = TextMuted,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // NEXT CANDLE ENTRY INSTRUCTION Box (no live time or countdown)
                    GlassCard(
                        borderColor = accent.copy(alpha = 0.65f),
                        contentPadding = PaddingValues(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Schedule,
                                contentDescription = "Next candle instruction",
                                tint = accent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "NEXT CANDLE FORECAST",
                                color = accent,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isBuy) "NEXT CANDLE: BUY (LONG)" else "NEXT CANDLE: SELL (SHORT)",
                            color = TextPrimary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Wait for the running candle to close. Enter ${if (isBuy) "BUY (LONG)" else "SELL (SHORT)"} at the next candle open.",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    QuantumGlowingButton(
                        text = "NEW SIGNAL",
                        testTag = "new_signal_button",
                        onClick = onNewSignal
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    QuantumOutlineButton(
                        text = "Change Pair",
                        testTag = "change_pair_button",
                        onClick = onChangePair
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
