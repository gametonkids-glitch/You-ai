package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.components.YouTubeLoginDialog
import com.example.viewmodel.ShortsViewModel

@Composable
fun YouTubeSettingsScreen(
    viewModel: ShortsViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = viewModel.preferences

    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val channelTitle by viewModel.channelTitle.collectAsState()
    val channelHandle by viewModel.channelHandle.collectAsState()
    val channelSubscribers by viewModel.channelSubscribers.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()

    var tokenInput by remember { mutableStateOf(prefs.accessToken) }
    var channelNameInput by remember { mutableStateOf(channelTitle) }
    var isSimMode by remember { mutableStateOf(prefs.isSimulatorMode) }
    var showLoginDialog by remember { mutableStateOf(false) }

    var isVerifying by remember { mutableStateOf(false) }

    if (showLoginDialog) {
        YouTubeLoginDialog(
            currentEmail = userEmail,
            currentChannelTitle = channelTitle,
            currentHandle = channelHandle,
            onDismiss = { showLoginDialog = false },
            onLoginSuccess = { email, name, handle, subs, token ->
                viewModel.loginChannel(email, name, handle, subs, token)
                channelNameInput = name
                tokenInput = token
                Toast.makeText(context, "Logged into $name successfully!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "YouTube Connectivity & Account",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "Manage your destination YouTube channel login and upload connectivity",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // YouTube Channel Profile Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFF0033),
                        modifier = Modifier.size(54.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isLoggedIn) channelTitle else "Not Connected",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        if (isLoggedIn) {
                            Text(
                                text = "$channelHandle • $channelSubscribers",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = userEmail,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(
                                text = "Please sign in to link your YouTube channel",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isLoggedIn) Icons.Default.CheckCircle else Icons.Default.HelpOutline,
                                contentDescription = null,
                                tint = if (isLoggedIn) Color(0xFF06D6A0) else MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isLoggedIn) "Channel Connected & Verified" else "Account Disconnected",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isLoggedIn) Color(0xFF06D6A0) else MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (isLoggedIn) {
                        OutlinedButton(
                            onClick = { viewModel.logoutChannel() },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("logout_channel_button")
                        ) {
                            Icon(imageVector = Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Disconnect")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { showLoginDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("switch_channel_button")
                        ) {
                            Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Switch Account")
                        }
                    } else {
                        Button(
                            onClick = { showLoginDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0033)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_channel_button")
                        ) {
                            Icon(imageVector = Icons.Default.Login, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sign In with YouTube / Google", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Simulator Mode Toggle
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFFB703).copy(alpha = 0.2f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Science,
                                contentDescription = null,
                                tint = Color(0xFFFFB703),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Test / Simulator Mode",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Test uploads and countdown queue without consuming YouTube Data API quota",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Switch(
                    checked = isSimMode,
                    onCheckedChange = {
                        isSimMode = it
                        prefs.isSimulatorMode = it
                    },
                    modifier = Modifier.testTag("simulator_mode_switch")
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // YouTube OAuth2 / API Token Section
        Text(
            text = "YouTube Data API v3 Direct Token",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "For direct live uploads to your channel, you can configure your Google OAuth2 Bearer Access Token with 'youtube.upload' scope.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = tokenInput,
            onValueChange = { tokenInput = it },
            placeholder = { Text("ya29.a0AfH6...") },
            label = { Text("OAuth2 Bearer Access Token") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Key, contentDescription = null)
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("youtube_token_input"),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isVerifying = true
                viewModel.saveYouTubeSettings(
                    token = tokenInput,
                    isSimMode = isSimMode,
                    channelName = channelNameInput
                ) { success, msg ->
                    isVerifying = false
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                }
            },
            enabled = !isVerifying,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("save_youtube_settings_button"),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isVerifying) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Verifying...")
            } else {
                Text("Verify & Save Settings", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Hindi Guide Card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "YouTube कनेक्टिविटी और लॉगिन निर्देश",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "• आप 'Switch Account' या 'Sign In' बटन दबाकर तुरंत अपने YouTube चैनल को लिंक कर सकते हैं।",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "• जो भी चैनल यहाँ कनेक्टेड होगा, सारे Shorts सीधे उसी चैनल पर शेड्यूल और अपलोड होंगे।",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "• वीडियो चुनते ही AI अपने आप टाइटल, विवरण (Description) और टैग्स जनरेट कर देता है।",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
