package com.example

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.AnalyzingScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LockScreen
import com.example.ui.screens.PairSelectionScreen
import com.example.ui.screens.PhotoAnalysisScreen
import com.example.ui.screens.SelectDirectionScreen
import com.example.ui.screens.SignalResultScreen
import com.example.ui.screens.TimeframeSelectionScreen
import com.example.ui.theme.TSQuantumTheme
import com.example.ui.viewmodel.QuantumViewModel

object QuantumRoutes {
    const val ROUTE_LOCK = "lock"
    const val ROUTE_DASHBOARD = "dashboard"
    const val ROUTE_PHOTO_ANALYSIS = "photo_analysis"
    const val ROUTE_DIRECTION = "direction"
    const val ROUTE_PAIR_SELECTION = "pair_selection"
    const val ROUTE_TIMEFRAME = "timeframe"
    const val ROUTE_ANALYZING = "analyzing"
    const val ROUTE_RESULT = "result"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Configure Firebase App Check with launch intent before any UI or ViewModels
        configureAppCheck(this, intent)

        // STEP 2: Enable edge-to-edge with dark crimson-black system bars
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        // STEP 3: Render Compose UI
        setContent {
            TSQuantumTheme {
                TSQuantumApp()
            }
        }
    }
}

@Composable
fun TSQuantumApp(
    quantumViewModel: QuantumViewModel = viewModel()
) {
    val navController = rememberNavController()
    val uiState by quantumViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isUnlocked) {
        if (uiState.isUnlocked && navController.currentDestination?.route == QuantumRoutes.ROUTE_LOCK) {
            navController.navigate(QuantumRoutes.ROUTE_DASHBOARD) {
                popUpTo(QuantumRoutes.ROUTE_LOCK) { inclusive = true }
            }
        }
    }

    LaunchedEffect(uiState.pendingResultNavigationSignalId) {
        val pendingId = uiState.pendingResultNavigationSignalId
        if (pendingId != null && uiState.currentSignal != null) {
            quantumViewModel.consumeResultNavigation()
            navController.navigate(QuantumRoutes.ROUTE_RESULT) {
                popUpTo(QuantumRoutes.ROUTE_ANALYZING) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = QuantumRoutes.ROUTE_LOCK
    ) {
        composable(QuantumRoutes.ROUTE_LOCK) {
            LockScreen(
                passwordInput = uiState.passwordInput,
                isChecking = uiState.isCheckingPassword,
                errorMessage = uiState.passwordError,
                selectedPaymentTab = uiState.selectedPaymentTab,
                onPasswordChange = quantumViewModel::onPasswordChange,
                onUnlockClick = quantumViewModel::submitPassword,
                onSelectPaymentTab = quantumViewModel::onPaymentTabSelected
            )
        }

        composable(QuantumRoutes.ROUTE_DASHBOARD) {
            DashboardScreen(
                uiState = uiState,
                onSelectBottomTab = quantumViewModel::onBottomTabSelected,
                onOpenPhotoAnalysis = {
                    navController.navigate(QuantumRoutes.ROUTE_PHOTO_ANALYSIS)
                },
                onOpenManualAnalysis = {
                    navController.navigate(QuantumRoutes.ROUTE_DIRECTION)
                },
                onRefreshSetups = quantumViewModel::refreshBestSetups,
                onClickBestSetup = { setup ->
                    quantumViewModel.runQuickSetupAnalysis(setup.pair, setup.timeframe)
                    navController.navigate(QuantumRoutes.ROUTE_ANALYZING)
                },
                onRefreshMarkets = quantumViewModel::refreshMarketQuotes,
                onFilterMarkets = quantumViewModel::setMarketFilter,
                onClickMarketQuote = { quote ->
                    quantumViewModel.selectPair(quote.pair)
                    navController.navigate(QuantumRoutes.ROUTE_TIMEFRAME)
                }
            )
        }

        composable(QuantumRoutes.ROUTE_PHOTO_ANALYSIS) {
            PhotoAnalysisScreen(
                liveTradersCount = uiState.liveTradersCount,
                selectedBitmap = uiState.selectedChartBitmap,
                isAnalyzing = uiState.isAnalyzingPhoto,
                errorMessage = uiState.photoAnalysisError,
                onBack = { navController.popBackStack() },
                onBitmapSelected = quantumViewModel::onChartBitmapSelected,
                onAnalyzeClick = {
                    quantumViewModel.analyzeSelectedChartPhoto()
                }
            )

            LaunchedEffect(uiState.pendingAnalyzingNavigation) {
                if (uiState.pendingAnalyzingNavigation &&
                    navController.currentDestination?.route == QuantumRoutes.ROUTE_PHOTO_ANALYSIS
                ) {
                    quantumViewModel.consumeAnalyzingNavigation()
                    navController.navigate(QuantumRoutes.ROUTE_ANALYZING)
                }
            }
        }

        composable(QuantumRoutes.ROUTE_DIRECTION) {
            SelectDirectionScreen(
                liveTradersCount = uiState.liveTradersCount,
                onBack = { navController.popBackStack() },
                onSelectDirection = { direction ->
                    quantumViewModel.selectDirection(direction)
                    navController.navigate(QuantumRoutes.ROUTE_PAIR_SELECTION)
                }
            )
        }

        composable(QuantumRoutes.ROUTE_PAIR_SELECTION) {
            PairSelectionScreen(
                liveTradersCount = uiState.liveTradersCount,
                direction = uiState.selectedDirection,
                onBack = { navController.popBackStack() },
                onSelectPair = { pair ->
                    quantumViewModel.selectPair(pair)
                    navController.navigate(QuantumRoutes.ROUTE_TIMEFRAME)
                }
            )
        }

        composable(QuantumRoutes.ROUTE_TIMEFRAME) {
            TimeframeSelectionScreen(
                liveTradersCount = uiState.liveTradersCount,
                pair = uiState.selectedPair,
                direction = uiState.selectedDirection,
                onBack = { navController.popBackStack() },
                onStartTimeframe = { option ->
                    quantumViewModel.selectTimeframeAndAnalyze(option)
                    navController.navigate(QuantumRoutes.ROUTE_ANALYZING)
                }
            )
        }

        composable(QuantumRoutes.ROUTE_ANALYZING) {
            AnalyzingScreen(
                liveTradersCount = uiState.liveTradersCount,
                analyzingState = uiState.analyzingState
            )
        }

        composable(QuantumRoutes.ROUTE_RESULT) {
            val signal = uiState.currentSignal
            if (signal != null) {
                SignalResultScreen(
                    liveTradersCount = uiState.liveTradersCount,
                    signal = signal,
                    onNewSignal = {
                        quantumViewModel.regenerateCurrentSignal()
                        navController.navigate(QuantumRoutes.ROUTE_ANALYZING) {
                            popUpTo(QuantumRoutes.ROUTE_RESULT) { inclusive = true }
                        }
                    },
                    onChangePair = {
                        navController.navigate(QuantumRoutes.ROUTE_DIRECTION) {
                            popUpTo(QuantumRoutes.ROUTE_DASHBOARD) { inclusive = false }
                        }
                    }
                )
            }
        }
    }
}
