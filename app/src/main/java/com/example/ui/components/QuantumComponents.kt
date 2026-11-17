package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.model.MainBottomTab
import com.example.ui.theme.BinanceYellow
import com.example.ui.theme.CrimsonBgDeep
import com.example.ui.theme.CrimsonBgLight
import com.example.ui.theme.CrimsonBgMid
import com.example.ui.theme.DarkRedOutline
import com.example.ui.theme.DeepRedButton
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassCardBg
import com.example.ui.theme.NagadOrange
import com.example.ui.theme.NeonRed
import com.example.ui.theme.NeonRedGlow
import com.example.ui.theme.SignalGreen
import com.example.ui.theme.TelegramBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun QuantumBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        CrimsonBgDeep,
                        CrimsonBgMid,
                        CrimsonBgLight,
                        CrimsonBgDeep
                    )
                )
            )
    ) {
        // Subtle radial crimson ambient glow at top
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x28FF2A2A),
                        Color(0x00FF2A2A)
                    ),
                    center = Offset(size.width * 0.5f, size.height * 0.14f),
                    radius = size.width * 0.85f
                ),
                center = Offset(size.width * 0.5f, size.height * 0.14f),
                radius = size.width * 0.85f
            )
        }
        content()
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    borderColor: Color = GlassBorderSubtle,
    cornerRadius: Dp = 18.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(cornerRadius),
        color = GlassCardBg,
        border = BorderStroke(1.dp, borderColor),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}

@Composable
fun QuantumEmblem(
    size: Dp,
    modifier: Modifier = Modifier,
    pulseGlow: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "emblemPulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = if (pulseGlow) 18.dp else 6.dp,
                shape = CircleShape,
                ambientColor = NeonRed,
                spotColor = NeonRed
            )
            .clip(CircleShape)
            .border(
                width = if (size > 64.dp) 2.5.dp else 1.5.dp,
                color = NeonRed.copy(alpha = if (pulseGlow) glowAlpha else 0.85f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ts_quantum_icon_1794930678382),
            contentDescription = "TS Quantum Emblem",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun StickyTopHeader(
    liveUsersCount: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "liveDot")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xD9160404),
        border = BorderStroke(0.5.dp, GlassBorderSubtle)
    ) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuantumEmblem(size = 34.dp, pulseGlow = false)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = TextPrimary, fontWeight = FontWeight.ExtraBold)) {
                            append("TS ")
                        }
                        withStyle(SpanStyle(color = NeonRed, fontWeight = FontWeight.ExtraBold)) {
                            append("QUANTUM")
                        }
                    },
                    style = MaterialTheme.typography.titleMedium,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0x33FF2A2A),
                    border = BorderStroke(1.dp, NeonRed)
                ) {
                    Text(
                        text = "V2.4.7",
                        color = NeonRed,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .alpha(dotAlpha)
                            .clip(CircleShape)
                            .background(SignalGreen)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "LIVE AI",
                        color = SignalGreen,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0x662D0A0A),
                    border = BorderStroke(1.dp, GlassBorderSubtle)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Sensors,
                            contentDescription = "Active live traders",
                            tint = TextSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = liveUsersCount.toString(),
                            color = TextPrimary,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StickyBottomNavBar(
    activeTab: MainBottomTab,
    onSelectTab: (MainBottomTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xEE170404),
        border = BorderStroke(0.75.dp, GlassBorderSubtle)
    ) {
        Row(
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavTabItem(
                label = "TRADE",
                selected = activeTab == MainBottomTab.TRADE,
                testTag = "bottom_tab_trade",
                icon = {
                    Icon(
                        imageVector = Icons.Filled.ShowChart,
                        contentDescription = "Trade tab",
                        tint = if (activeTab == MainBottomTab.TRADE) NeonRed else TextMuted,
                        modifier = Modifier.size(22.dp)
                    )
                },
                onClick = { onSelectTab(MainBottomTab.TRADE) }
            )

            BottomNavTabItem(
                label = "MARKETS",
                selected = activeTab == MainBottomTab.MARKETS,
                testTag = "bottom_tab_markets",
                icon = {
                    Icon(
                        imageVector = if (activeTab == MainBottomTab.MARKETS) Icons.Filled.Star else Icons.Outlined.StarOutline,
                        contentDescription = "Markets tab",
                        tint = if (activeTab == MainBottomTab.MARKETS) NeonRed else TextMuted,
                        modifier = Modifier.size(22.dp)
                    )
                },
                onClick = { onSelectTab(MainBottomTab.MARKETS) }
            )
        }
    }
}

@Composable
private fun BottomNavTabItem(
    label: String,
    selected: Boolean,
    testTag: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) Color(0x2EFF2A2A) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 28.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        icon()
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            color = if (selected) NeonRed else TextMuted,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun QuantumGlowingButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testTag: String = ""
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier)
            .shadow(
                elevation = if (enabled) 16.dp else 0.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = NeonRedGlow,
                spotColor = NeonRed
            ),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = NeonRed,
            contentColor = Color.White,
            disabledContainerColor = DeepRedButton.copy(alpha = 0.45f),
            disabledContentColor = Color.White.copy(alpha = 0.7f)
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun QuantumOutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "",
    leadingIcon: (@Composable () -> Unit)? = null
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.2.dp, DarkRedOutline),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color(0x66220606),
            contentColor = TextPrimary
        )
    ) {
        if (leadingIcon != null) {
            leadingIcon()
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun BinanceDiamondIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(18.dp)) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val r = size.minDimension * 0.42f
        val path = Path().apply {
            moveTo(cx, cy - r)
            lineTo(cx + r, cy)
            lineTo(cx, cy + r)
            lineTo(cx - r, cy)
            close()
        }
        drawPath(path, color = BinanceYellow, style = Stroke(width = 2.4f))
        val innerR = r * 0.42f
        val inner = Path().apply {
            moveTo(cx, cy - innerR)
            lineTo(cx + innerR, cy)
            lineTo(cx, cy + innerR)
            lineTo(cx - innerR, cy)
            close()
        }
        drawPath(inner, color = BinanceYellow)
    }
}

@Composable
fun NagadIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(18.dp)) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val radius = size.minDimension * 0.44f
        drawCircle(color = NagadOrange, radius = radius, center = Offset(cx, cy))
        val innerPath = Path().apply {
            moveTo(cx - radius * 0.35f, cy + radius * 0.3f)
            lineTo(cx, cy - radius * 0.45f)
            lineTo(cx + radius * 0.35f, cy + radius * 0.3f)
            close()
        }
        drawPath(innerPath, color = Color.White)
    }
}

@Composable
fun TelegramPaperPlaneIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(18.dp)) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.92f, h * 0.14f)
            lineTo(w * 0.08f, h * 0.48f)
            lineTo(w * 0.40f, h * 0.62f)
            lineTo(w * 0.72f, h * 0.88f)
            close()
        }
        drawPath(path, color = TelegramBlue)
        drawLine(
            color = Color.White,
            start = Offset(w * 0.40f, h * 0.62f),
            end = Offset(w * 0.86f, h * 0.20f),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun QuantumAiBrainGlyph(
    modifier: Modifier = Modifier,
    color: Color = NeonRed
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        // Left & Right brain hemispheres stylized as circuit lobes
        drawArc(
            color = color,
            startAngle = 95f,
            sweepAngle = 170f,
            useCenter = false,
            topLeft = Offset(w * 0.16f, h * 0.16f),
            size = androidx.compose.ui.geometry.Size(w * 0.38f, h * 0.68f),
            style = Stroke(width = 5f, cap = StrokeCap.Round)
        )
        drawArc(
            color = color,
            startAngle = -85f,
            sweepAngle = 170f,
            useCenter = false,
            topLeft = Offset(w * 0.46f, h * 0.16f),
            size = androidx.compose.ui.geometry.Size(w * 0.38f, h * 0.68f),
            style = Stroke(width = 5f, cap = StrokeCap.Round)
        )

        // Central spine
        drawLine(
            color = color,
            start = Offset(cx, h * 0.18f),
            end = Offset(cx, h * 0.82f),
            strokeWidth = 4f,
            cap = StrokeCap.Round
        )

        // Synaptic nodes
        val nodes = listOf(
            Offset(cx - w * 0.18f, cy - h * 0.14f),
            Offset(cx - w * 0.22f, cy + h * 0.08f),
            Offset(cx + w * 0.18f, cy - h * 0.14f),
            Offset(cx + w * 0.22f, cy + h * 0.08f),
            Offset(cx, cy)
        )
        nodes.forEach { n ->
            drawLine(color = color.copy(alpha = 0.7f), start = Offset(cx, cy), end = n, strokeWidth = 2.5f)
            drawCircle(color = color, radius = 5.5f, center = n)
        }
    }
}
