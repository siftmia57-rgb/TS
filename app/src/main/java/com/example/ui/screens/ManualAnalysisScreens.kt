package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.MarketDirection
import com.example.domain.model.TimeframeOption
import com.example.domain.model.TradingCatalog
import com.example.ui.components.GlassCard
import com.example.ui.components.QuantumBackground
import com.example.ui.components.StickyTopHeader
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.NeonRed
import com.example.ui.theme.SignalGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SelectDirectionScreen(
    liveTradersCount: Int,
    onBack: () -> Unit,
    onSelectDirection: (MarketDirection) -> Unit
) {
    BackHandler(onBack = onBack)

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
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(modifier = Modifier.widthIn(max = 580.dp)) {
                    BackTopRow(onBack = onBack)

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "SELECT TRADING DIRECTION",
                        color = TextPrimary,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Choose your preferred market for AI-powered signals",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "• 94.7% WIN  • 50K+ TRADERS  • REAL TIME",
                        color = NeonRed,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Card 1: OTC PAIRS
                    DirectionCard(
                        title = "OTC PAIRS",
                        badge = "HOT",
                        subtitle = "Forex & currency pairs • Real-time OTC analysis",
                        tags = listOf("30 Assets", "24/7 Trading", "Forex • Crypto", "5s-5m"),
                        footerLeft = "• Active Markets",
                        footerRight = "High Volume",
                        testTag = "direction_otc_card",
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Bolt,
                                contentDescription = "OTC Pairs",
                                tint = NeonRed,
                                modifier = Modifier.size(26.dp)
                            )
                        },
                        onClick = { onSelectDirection(MarketDirection.OTC) }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Card 2: MARKET PAIRS
                    DirectionCard(
                        title = "MARKET PAIRS",
                        badge = "PRO",
                        subtitle = "Real exchange assets • Forex, Stocks, Indices",
                        tags = listOf("24 Assets", "Market Hours", "Stocks • Indices", "1-5 min"),
                        footerLeft = "• Market Open",
                        footerRight = "Real Exchange",
                        testTag = "direction_market_card",
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Public,
                                contentDescription = "Market Pairs",
                                tint = NeonRed,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        onClick = { onSelectDirection(MarketDirection.MARKET) }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "• Markets Open - Select a direction to begin analysis | Powered by TS Quantum Neural Engine",
                        color = TextMuted,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DirectionCard(
    title: String,
    badge: String,
    subtitle: String,
    tags: List<String>,
    footerLeft: String,
    footerRight: String,
    testTag: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        contentPadding = PaddingValues(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x33FF2A2A)),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Surface(
                shape = RoundedCornerShape(50),
                color = NeonRed
            ) {
                Text(
                    text = badge,
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = subtitle,
            color = TextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tags.forEach { tag ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0x80120303),
                    border = BorderStroke(1.dp, GlassBorderSubtle)
                ) {
                    Text(
                        text = tag,
                        color = TextPrimary,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = footerLeft,
                color = SignalGreen,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = footerRight,
                color = NeonRed,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PairSelectionScreen(
    liveTradersCount: Int,
    direction: MarketDirection,
    onBack: () -> Unit,
    onSelectPair: (String) -> Unit
) {
    BackHandler(onBack = onBack)

    val allPairs = remember(direction) {
        if (direction == MarketDirection.OTC) TradingCatalog.otcPairs else TradingCatalog.marketPairs
    }
    var searchQuery by remember { mutableStateOf("") }
    val filteredPairs = remember(allPairs, searchQuery) {
        if (searchQuery.isBlank()) allPairs
        else allPairs.filter { it.contains(searchQuery.trim(), ignoreCase = true) }
    }

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
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(modifier = Modifier.widthIn(max = 600.dp)) {
                    BackTopRow(onBack = onBack)

                    Text(
                        text = if (direction == MarketDirection.OTC) "SELECT OTC PAIR" else "SELECT MARKET PAIR",
                        color = TextPrimary,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${allPairs.size} live Quantum scanner pairs available",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text("Search asset (e.g. EUR/USD, GOLD, TESLA)", color = TextMuted)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search pairs",
                                tint = NeonRed
                            )
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pair_search_input"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0x99120303),
                            unfocusedContainerColor = Color(0x80120303),
                            focusedBorderColor = NeonRed,
                            unfocusedBorderColor = GlassBorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 18.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(filteredPairs, key = { it }) { pair ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .testTag("pair_pill_$pair")
                                    .clip(RoundedCornerShape(50))
                                    .clickable { onSelectPair(pair) },
                                shape = RoundedCornerShape(50),
                                color = Color(0xA62D0A0A),
                                border = BorderStroke(1.dp, GlassBorderSubtle)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(horizontal = 10.dp)
                                ) {
                                    Text(
                                        text = pair,
                                        color = TextPrimary,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimeframeSelectionScreen(
    liveTradersCount: Int,
    pair: String,
    direction: MarketDirection,
    onBack: () -> Unit,
    onStartTimeframe: (TimeframeOption) -> Unit
) {
    BackHandler(onBack = onBack)

    val timeframes = remember(direction, pair) {
        if (direction == MarketDirection.OTC || pair.endsWith("OTC", ignoreCase = true)) {
            TradingCatalog.otcTimeframes
        } else {
            TradingCatalog.marketTimeframes
        }
    }

    var pendingConfirmOption by remember { mutableStateOf<TimeframeOption?>(null) }

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
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(modifier = Modifier.widthIn(max = 600.dp)) {
                    BackTopRow(onBack = onBack)

                    Text(
                        text = "SELECT TIMEFRAME",
                        color = TextPrimary,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Choose expiration window for $pair",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(timeframes, key = { it.label }) { option ->
                            GlassCard(
                                modifier = Modifier.testTag("timeframe_card_${option.label}"),
                                contentPadding = PaddingValues(14.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clip(CircleShape)
                                            .background(Color(0x33FF2A2A)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.AccessTime,
                                            contentDescription = option.label,
                                            tint = NeonRed,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = option.label,
                                        color = TextPrimary,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = option.subtitle,
                                    color = TextSecondary,
                                    style = MaterialTheme.typography.bodySmall,
                                    minLines = 2,
                                    maxLines = 2
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = { pendingConfirmOption = option },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp)
                                        .testTag("start_timeframe_${option.label}"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = NeonRed,
                                        contentColor = Color.White
                                    ),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(
                                        text = "START",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.8.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    GlassCard(contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)) {
                        Text(
                            text = "Selected pair: $pair",
                            color = TextPrimary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Choose timeframe above to run confirmation analysis",
                            color = TextMuted,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
    }

    val confirmOption = pendingConfirmOption
    if (confirmOption != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { pendingConfirmOption = null },
            containerColor = Color(0xFF1E0606),
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary,
            title = {
                Text(
                    text = "CONFIRMATION ANALYSIS",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Pair: $pair  •  Timeframe: ${confirmOption.label}",
                        color = TextPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "TS Quantum will verify all 4 confirmation layers before issuing BUY (LONG) or SELL (SHORT):",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0x80120303),
                        border = BorderStroke(1.dp, GlassBorderSubtle)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text("✓ EMA 9 & 21 Trend Crossover", color = SignalGreen, style = MaterialTheme.typography.bodySmall)
                            Text("✓ RSI(14) Overbought / Oversold Filter", color = SignalGreen, style = MaterialTheme.typography.bodySmall)
                            Text("✓ MACD Momentum & HTF SNR Breakout", color = SignalGreen, style = MaterialTheme.typography.bodySmall)
                            Text("✓ Gemini AI Final Signal Confirmation", color = SignalGreen, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        pendingConfirmOption = null
                        onStartTimeframe(confirmOption)
                    },
                    modifier = Modifier.testTag("confirm_analyze_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "CONFIRM & ANALYZE",
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingConfirmOption = null }) {
                    Text(text = "Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun BackTopRow(onBack: () -> Unit) {
    TextButton(
        onClick = onBack,
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
        modifier = Modifier.testTag("manual_back_button")
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = TextPrimary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "Back",
            color = TextPrimary,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
