package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.components.GlassCard
import com.example.ui.components.QuantumBackground
import com.example.ui.components.QuantumGlowingButton
import com.example.ui.components.QuantumOutlineButton
import com.example.ui.components.StickyTopHeader
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.NeonRed
import com.example.ui.theme.NeonRedBright
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PhotoAnalysisScreen(
    liveTradersCount: Int,
    selectedBitmap: Bitmap?,
    isAnalyzing: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onBitmapSelected: (Bitmap) -> Unit,
    onAnalyzeClick: () -> Unit
) {
    val context = LocalContext.current

    BackHandler(enabled = !isAnalyzing, onBack = onBack)

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val decoded = decodeSampledBitmap(context, uri)
            if (decoded != null) {
                onBitmapSelected(decoded)
            }
        }
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
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(modifier = Modifier.widthIn(max = 580.dp)) {
                    TextButton(
                        onClick = onBack,
                        enabled = !isAnalyzing,
                        modifier = Modifier.testTag("photo_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Back",
                            color = TextPrimary,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "AI PHOTO ANALYSIS",
                            color = TextPrimary,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = NeonRed
                        ) {
                            Text(
                                text = "GEMINI VISION",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Upload a Quotex or TradingView chart screenshot — AI detects the pair, candlestick trend, SNR/FVG zones & outputs a signal",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    GlassCard(
                        modifier = Modifier
                            .testTag("photo_upload_zone")
                            .clip(RoundedCornerShape(18.dp))
                            .clickable(enabled = !isAnalyzing) {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        borderColor = NeonRed.copy(alpha = 0.65f),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        if (selectedBitmap != null) {
                            Image(
                                bitmap = selectedBitmap.asImageBitmap(),
                                contentDescription = "Uploaded chart screenshot",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF0E0202))
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Tap image to pick a different chart screenshot",
                                color = TextMuted,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(210.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0x66120303)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(62.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color(0x33FF2A2A)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.AddPhotoAlternate,
                                            contentDescription = "Upload chart",
                                            tint = NeonRed,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Tap to Upload Chart Screenshot",
                                        color = TextPrimary,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Supports Quotex, TradingView & MetaTrader screenshots",
                                        color = TextMuted,
                                        style = MaterialTheme.typography.bodySmall,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x40FF2A2A),
                            border = BorderStroke(1.dp, NeonRedBright)
                        ) {
                            Text(
                                text = errorMessage,
                                color = TextPrimary,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    if (isAnalyzing) {
                        GlassCard(contentPadding = PaddingValues(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = NeonRed,
                                    strokeWidth = 2.4.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Gemini Vision scanning pair, candlesticks & SNR/FVG zones...",
                                    color = TextPrimary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    } else {
                        if (selectedBitmap == null) {
                            QuantumGlowingButton(
                                text = "UPLOAD CHART SCREENSHOT",
                                testTag = "pick_chart_button",
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                            )
                        } else {
                            QuantumGlowingButton(
                                text = "ANALYZE CHART WITH AI",
                                testTag = "analyze_photo_button",
                                onClick = onAnalyzeClick
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            QuantumOutlineButton(
                                text = "Choose Another Screenshot",
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.AutoAwesome,
                                        contentDescription = null,
                                        tint = NeonRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun decodeSampledBitmap(context: android.content.Context, uri: Uri, maxDim: Int = 1440): Bitmap? {
    return try {
        val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, boundsOptions)
        }
        var sampleSize = 1
        while (boundsOptions.outWidth / sampleSize > maxDim || boundsOptions.outHeight / sampleSize > maxDim) {
            sampleSize *= 2
        }
        val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, decodeOptions)
        }
    } catch (_: Exception) {
        null
    }
}
