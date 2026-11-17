package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.BestSetupItem
import com.example.domain.model.MainBottomTab
import com.example.domain.model.MarketDirection
import com.example.domain.model.MarketQuoteItem
import com.example.domain.model.SignalDirection
import com.example.ui.components.GlassCard
import com.example.ui.components.QuantumBackground
import com.example.ui.components.StickyBottomNavBar
import com.example.ui.components.StickyTopHeader
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.NeonRed
import com.example.ui.theme.SignalGreen
import com.example.ui.theme.SignalGreenBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.QuantumUiState
import java.util.Locale

@Composable
fun DashboardScreen(
    uiState: QuantumUiState,
    onSelectBottomTab: (MainBottomTab) -> Unit,
    onOpenPhotoAnalysis: () -> Unit,
    onOpenManualAnalysis: () -> Unit,
    onRefreshSetups: () -> Unit,
    onClickBestSetup: (BestSetupItem) -> Unit,
    onRefreshMarkets: () -> Unit,
    onFilterMarkets: (MarketDirection?) -> Unit,
    onClickMarketQuote: (MarketQuoteItem) -> Unit
) {
    QuantumBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            StickyTopHeader(liveUsersCount = uiState.liveTradersCount)

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                if (uiState.activeBottomTab == MainBottomTab.TRADE) {
                    TradeTabContent(
                        uiState = uiState,
                        onOpenPhotoAnalysis = onOpenPhotoAnalysis,
                        onOpenManualAnalysis = onOpenManualAnalysis,
                        onRefreshSetups = onRefreshSetups,
                        onClickBestSetup = onClickBestSetup
                    )
                } else {
                    MarketsTabContent(
                        uiState = uiState,
                        onRefreshMarkets = onRefreshMarkets,
                        onFilterMarkets = onFilterMarkets,
                        onClickMarketQuote = onClickMarketQuote
                    )
                }
            }

            StickyBottomNavBar(
                activeTab = uiState.activeBottomTab,
                onSelectTab = onSelectBottomTab
            )
        }
    }
}

@Composable
private fun TradeTabContent(
    uiState: QuantumUiState,
    onOpenPhotoAnalysis: () -> Unit,
    onOpenManualAnalysis: () -> Unit,
    onRefreshSetups: () -> Unit,
    onClickBestSetup: (BestSetupItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .widthIn(max = 600.dp)
            .fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Welcome Banner
        item {
            Column(modifier = Modifier.padding(bottom = 4.dp)) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = TextPrimary, fontWeight = FontWeight.ExtraBold)) {
                            append("WELCOME, ")
                        }
                        withStyle(SpanStyle(color = NeonRed, fontWeight = FontWeight.ExtraBold)) {
                            append("ELITE TRADER")
                        }
                    },
                    style = MaterialTheme.typography.headlineLarge,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Choose how the Quantum engine finds your next signal",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Mode Card 1: Photo Analysis
        item {
            ModeActionCard(
                title = "Photo Analysis",
                subtitle = "Upload a chart screenshot — AI reads the pair & gives a signal",
                badgeText = "NEW",
                testTag = "photo_analysis_card",
                icon = {
                    Icon(
                        imageVector = Icons.Filled.CameraAlt,
                        contentDescription = "Photo Analysis",
                        tint = NeonRed,
                        modifier = Modifier.size(24.dp)
                    )
                },
                onClick = onOpenPhotoAnalysis
            )
        }

        // Mode Card 2: Manual Analysis
        item {
            ModeActionCard(
                title = "Manual Analysis",
                subtitle = "Pick pair & timeframe yourself — classic AI signal flow",
                badgeText = null,
                testTag = "manual_analysis_card",
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Bolt,
                        contentDescription = "Manual Analysis",
                        tint = NeonRed,
                        modifier = Modifier.size(26.dp)
                    )
                },
                onClick = onOpenManualAnalysis
            )
        }

        // Today's Best Setups Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.LocalFireDepartment,
                        contentDescription = "Best Setups",
                        tint = NeonRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "TODAY'S BEST SETUPS",
                        color = TextPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.6.sp
                    )
                }

                Surface(
                    modifier = Modifier
                        .testTag("refresh_setups_button")
                        .clip(RoundedCornerShape(50))
                        .clickable(enabled = !uiState.isRefreshingSetups, onClick = onRefreshSetups),
                    shape = RoundedCornerShape(50),
                    color = Color(0x40FF2A2A),
                    border = BorderStroke(1.dp, NeonRed.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (uiState.isRefreshingSetups) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                color = TextPrimary,
                                strokeWidth = 1.8.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Refresh Setups",
                                tint = TextPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "REFRESH",
                            color = TextPrimary,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (uiState.bestSetups.isEmpty() && uiState.isRefreshingSetups) {
            item {
                GlassCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 18.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = NeonRed,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Scanning TradingView technical feeds...",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        } else {
            items(uiState.bestSetups, key = { it.pair }) { setup ->
                BestSetupCard(
                    item = setup,
                    onClick = { onClickBestSetup(setup) }
                )
            }
        }
    }
}

@Composable
private fun ModeActionCard(
    title: String,
    subtitle: String,
    badgeText: String?,
    testTag: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        contentPadding = PaddingValues(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x33FF2A2A)),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        color = TextPrimary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    if (badgeText != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = NeonRed
                        ) {
                            Text(
                                text = badgeText,
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Open $title",
                tint = TextSecondary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun BestSetupCard(
    item: BestSetupItem,
    onClick: () -> Unit
) {
    val isCall = item.direction == SignalDirection.BUY
    val accent = if (isCall) SignalGreen else NeonRed
    val accentBg = if (isCall) SignalGreenBg else Color(0x2EFF2A2A)

    GlassCard(
        modifier = Modifier
            .testTag("setup_card_${item.pair}")
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(accentBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCall) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                    contentDescription = if (isCall) "CALL signal" else "PUT signal",
                    tint = accent,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.pair,
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isCall) "CALL signal" else "PUT signal",
                    color = accent,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Next candle • ${if (isCall) "BUY (LONG)" else "SELL (SHORT)"}",
                    color = TextMuted,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format(Locale.US, "%.1f%%", item.confidence),
                    color = TextPrimary,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "CONFIDENCE",
                    color = TextMuted,
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 0.8.sp
                )
            }
        }
    }
}

@Composable
private fun MarketsTabContent(
    uiState: QuantumUiState,
    onRefreshMarkets: () -> Unit,
    onFilterMarkets: (MarketDirection?) -> Unit,
    onClickMarketQuote: (MarketQuoteItem) -> Unit
) {
    val filtered = if (uiState.marketFilter == null) {
        uiState.marketQuotes
    } else {
        uiState.marketQuotes.filter { it.category == uiState.marketFilter }
    }

    LazyColumn(
        modifier = Modifier
            .widthIn(max = 600.dp)
            .fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "LIVE MARKETS SCANNER",
                        color = TextPrimary,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Real-time TradingView EMA & RSI(14) quotes",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable(enabled = !uiState.isLoadingMarkets, onClick = onRefreshMarkets),
                    shape = RoundedCornerShape(50),
                    color = Color(0x40FF2A2A),
                    border = BorderStroke(1.dp, NeonRed.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (uiState.isLoadingMarkets) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                color = TextPrimary,
                                strokeWidth = 1.8.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Refresh markets",
                                tint = TextPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "SYNC",
                            color = TextPrimary,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterPill("ALL", uiState.marketFilter == null) { onFilterMarkets(null) }
                FilterPill("OTC", uiState.marketFilter == MarketDirection.OTC) {
                    onFilterMarkets(MarketDirection.OTC)
                }
                FilterPill("EXCHANGE", uiState.marketFilter == MarketDirection.MARKET) {
                    onFilterMarkets(MarketDirection.MARKET)
                }
            }
        }

        items(filtered, key = { it.pair }) { quote ->
            val isBuy = quote.direction == SignalDirection.BUY
            val accent = if (isBuy) SignalGreen else NeonRed

            GlassCard(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onClickMarketQuote(quote) },
                contentPadding = PaddingValues(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = quote.pair,
                            color = TextPrimary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${quote.emaTrend} • RSI ${String.format(Locale.US, "%.1f", quote.rsi14)}",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = quote.feedSource,
                            color = TextMuted,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = formatPrice(quote.price),
                            color = TextPrimary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isBuy) SignalGreenBg else Color(0x33FF2A2A)
                        ) {
                            Text(
                                text = "${if (isBuy) "BUY" else "SELL"} (${
                                    String.format(Locale.US, "%+.2f%%", quote.changePercent)
                                })",
                                color = accent,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(50),
        color = if (selected) NeonRed else Color(0x662D0A0A),
        border = BorderStroke(1.dp, if (selected) NeonRed else GlassBorderSubtle)
    ) {
        Text(
            text = label,
            color = TextPrimary,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
        )
    }
}

fun formatPrice(price: Double): String {
    return when {
        price >= 1000.0 -> String.format(Locale.US, "%.2f", price)
        price >= 10.0 -> String.format(Locale.US, "%.3f", price)
        else -> String.format(Locale.US, "%.5f", price)
    }
}
