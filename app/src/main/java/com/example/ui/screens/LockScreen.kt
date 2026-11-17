package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.PaymentMethodTab
import com.example.ui.components.BinanceDiamondIcon
import com.example.ui.components.GlassCard
import com.example.ui.components.NagadIcon
import com.example.ui.components.QuantumBackground
import com.example.ui.components.QuantumEmblem
import com.example.ui.components.QuantumOutlineButton
import com.example.ui.components.TelegramPaperPlaneIcon
import com.example.ui.theme.BinanceYellow
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.NagadOrange
import com.example.ui.theme.NeonRed
import com.example.ui.theme.NeonRedBright
import com.example.ui.theme.NeonRedGlow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

private const val BINANCE_UID = "1270665725"
private const val NAGAD_NUMBER = "01835003603"
private const val TELEGRAM_HANDLE = "@tstraderonly"

@Composable
fun LockScreen(
    passwordInput: String,
    isChecking: Boolean,
    errorMessage: String?,
    selectedPaymentTab: PaymentMethodTab,
    onPasswordChange: (String) -> Unit,
    onUnlockClick: () -> Unit,
    onSelectPaymentTab: (PaymentMethodTab) -> Unit
) {
    val context = LocalContext.current
    var passwordVisible by remember { mutableStateOf(false) }

    QuantumBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.widthIn(max = 520.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Centered circular glowing red emblem
                QuantumEmblem(size = 114.dp, pulseGlow = true)

                Spacer(modifier = Modifier.height(18.dp))

                // Title: TS QUANTUM
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = TextPrimary, fontWeight = FontWeight.ExtraBold)) {
                            append("TS ")
                        }
                        withStyle(SpanStyle(color = NeonRed, fontWeight = FontWeight.ExtraBold)) {
                            append("QUANTUM")
                        }
                    },
                    style = MaterialTheme.typography.displayLarge,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = TextPrimary, fontWeight = FontWeight.Medium)) {
                            append("Welcome, ")
                        }
                        withStyle(SpanStyle(color = NeonRed, fontWeight = FontWeight.Bold)) {
                            append("Elite Trader")
                        }
                    },
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "TS TRADER",
                    color = TextMuted,
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 2.2.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Password Box Card
                GlassCard(
                    borderColor = if (errorMessage != null) NeonRedBright else GlassBorderSubtle,
                    contentPadding = PaddingValues(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "Password lock",
                            tint = NeonRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Enter Password",
                            color = TextPrimary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = onPasswordChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input"),
                        placeholder = {
                            Text(
                                text = "Enter access password",
                                color = TextMuted,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { onUnlockClick() }
                        ),
                        trailingIcon = {
                            IconButton(
                                onClick = { passwordVisible = !passwordVisible },
                                modifier = Modifier.testTag("toggle_password_visibility")
                            ) {
                                Icon(
                                    imageVector = if (passwordVisible) {
                                        Icons.Filled.VisibilityOff
                                    } else {
                                        Icons.Filled.Visibility
                                    },
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = TextSecondary
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xCC120303),
                            unfocusedContainerColor = Color(0xB3120303),
                            focusedBorderColor = NeonRed,
                            unfocusedBorderColor = Color(0x55FF2A2A),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = NeonRed
                        )
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage,
                            color = NeonRedBright,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.testTag("password_error_text")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onUnlockClick,
                        enabled = !isChecking,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("unlock_button")
                            .shadow(
                                elevation = 16.dp,
                                shape = RoundedCornerShape(14.dp),
                                ambientColor = NeonRedGlow,
                                spotColor = NeonRed
                            ),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonRed,
                            contentColor = Color.White,
                            disabledContainerColor = NeonRed.copy(alpha = 0.75f),
                            disabledContentColor = Color.White
                        )
                    ) {
                        if (isChecking) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "CHECKING...",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.2.sp
                            )
                        } else {
                            Text(
                                text = "UNLOCK",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.4.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Payment / Subscription Card
                GlassCard(contentPadding = PaddingValues(16.dp)) {
                    // Pill Toggle Tabs: Binance Pay | Nagad
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xB3120303))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PaymentPillTab(
                            title = "Binance Pay",
                            selected = selectedPaymentTab == PaymentMethodTab.BINANCE_PAY,
                            accentColor = BinanceYellow,
                            testTag = "tab_binance_pay",
                            modifier = Modifier.weight(1f),
                            icon = { BinanceDiamondIcon() },
                            onClick = { onSelectPaymentTab(PaymentMethodTab.BINANCE_PAY) }
                        )
                        PaymentPillTab(
                            title = "Nagad",
                            selected = selectedPaymentTab == PaymentMethodTab.NAGAD,
                            accentColor = NagadOrange,
                            testTag = "tab_nagad",
                            modifier = Modifier.weight(1f),
                            icon = { NagadIcon() },
                            onClick = { onSelectPaymentTab(PaymentMethodTab.NAGAD) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (selectedPaymentTab == PaymentMethodTab.BINANCE_PAY) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            BinanceDiamondIcon(modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "BINANCE UID",
                                color = BinanceYellow,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        CopyValueRow(
                            value = BINANCE_UID,
                            testTag = "copy_binance_uid",
                            onCopy = {
                                copyToClipboard(context, "Binance UID", BINANCE_UID)
                            }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Pay $10 via Binance Pay to the Binance UID above. After paying, take a screenshot as proof of payment and send it to our Telegram $TELEGRAM_HANDLE (search it on Telegram). You will then receive the password.",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "উপরের Binance UID-তে Binance Pay দিয়ে ১০ ডলার পেমেন্ট করুন। পেমেন্ট করার পর প্রমাণ হিসেবে একটি স্ক্রিনশট নিন এবং Telegram-এ $TELEGRAM_HANDLE সার্চ করে সেই স্ক্রিনশটটি পাঠান। তারপর আপনাকে পাসওয়ার্ড দেওয়া হবে।",
                            color = TextPrimary.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            NagadIcon(modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "NAGAD NUMBER (SEND MONEY)",
                                color = NagadOrange,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        CopyValueRow(
                            value = NAGAD_NUMBER,
                            testTag = "copy_nagad_number",
                            onCopy = {
                                copyToClipboard(context, "Nagad Number", NAGAD_NUMBER)
                            }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0x33F25C2A),
                            border = BorderStroke(1.dp, NagadOrange.copy(alpha = 0.7f))
                        ) {
                            Text(
                                text = "1325 TAKA",
                                color = NagadOrange,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Send Money 1325 TAKA via the Nagad app to the Nagad number above. After paying, take a screenshot as proof of payment and send it to our Telegram $TELEGRAM_HANDLE. You will then receive the password.",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Nagad app দিয়ে উপরের Nagad নম্বরে ১৩২৫ টাকা Send Money করুন। পেমেন্ট করার পর প্রমাণ হিসেবে একটি স্ক্রিনশট নিন এবং Telegram-এ $TELEGRAM_HANDLE সার্চ করে সেই স্ক্রিনশটটি পাঠান। তারপর আপনাকে পাসওয়ার্ড দেওয়া হবে।",
                            color = TextPrimary.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    QuantumOutlineButton(
                        text = "Telegram $TELEGRAM_HANDLE",
                        testTag = "telegram_contact_button",
                        leadingIcon = { TelegramPaperPlaneIcon() },
                        onClick = {
                            openTelegramOrCopy(context)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun PaymentPillTab(
    title: String,
    selected: Boolean,
    accentColor: Color,
    testTag: String,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(44.dp)
            .testTag(testTag)
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(50),
        color = if (selected) Color(0x55FF2A2A) else Color.Transparent,
        border = if (selected) BorderStroke(1.dp, accentColor.copy(alpha = 0.75f)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            icon()
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                color = if (selected) TextPrimary else TextMuted,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
private fun CopyValueRow(
    value: String,
    testTag: String,
    onCopy: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xCC120303),
        border = BorderStroke(1.dp, GlassBorderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = value,
                color = TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Surface(
                modifier = Modifier
                    .testTag(testTag)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onCopy),
                shape = RoundedCornerShape(8.dp),
                color = Color(0x3DFF2A2A),
                border = BorderStroke(1.dp, NeonRed.copy(alpha = 0.7f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.ContentCopy,
                        contentDescription = "Copy",
                        tint = NeonRed,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Copy",
                        color = TextPrimary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun copyToClipboard(context: Context, label: String, value: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText(label, value))
    Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show()
}

private fun openTelegramOrCopy(context: Context) {
    val handle = TELEGRAM_HANDLE.removePrefix("@").trim()
    val isPlaceholder = handle.startsWith("[") && handle.endsWith("]")
    if (isPlaceholder) {
        copyToClipboard(context, "Telegram", TELEGRAM_HANDLE)
        Toast.makeText(context, "Copied! Search $TELEGRAM_HANDLE on Telegram", Toast.LENGTH_SHORT).show()
    } else {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/$handle"))
            context.startActivity(intent)
        } catch (_: Exception) {
            copyToClipboard(context, "Telegram", TELEGRAM_HANDLE)
        }
    }
}
