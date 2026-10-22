package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun YouTubeLoginDialog(
    currentEmail: String,
    currentChannelTitle: String,
    currentHandle: String,
    onDismiss: () -> Unit,
    onLoginSuccess: (email: String, channelName: String, handle: String, subscribers: String, token: String) -> Unit
) {
    var email by remember { mutableStateOf(currentEmail.ifBlank { "creator@youtube.com" }) }
    var channelName by remember { mutableStateOf(currentChannelTitle.ifBlank { "My Shorts Channel" }) }
    var handle by remember { mutableStateOf(currentHandle.ifBlank { "@MyShortsCreator" }) }
    var token by remember { mutableStateOf("") }

    val presetChannels = listOf(
        Triple("Shorts Studio Official", "@ShortsStudioPro", "12.4K subscribers"),
        Triple("Tech & Hacks Shorts", "@TechHacksToday", "54.2K subscribers"),
        Triple("Comedy Reels Daily", "@ComedyReelsHub", "120K subscribers")
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with YouTube branding
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFF0033),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "YouTube Connectivity",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Sign in & connect destination channel",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick One-Tap Channel Profiles
                Text(
                    text = "Quick Connect Channels:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    presetChannels.forEach { (name, hdl, subs) ->
                        val isSelected = channelName == name
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    channelName = name
                                    handle = hdl
                                    email = "${hdl.removePrefix("@").lowercase()}@gmail.com"
                                }
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Tv,
                                        contentDescription = null,
                                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = name,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "$hdl • $subs",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Custom Login Inputs
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Google Account Email") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_email_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = channelName,
                    onValueChange = { channelName = it },
                    label = { Text("YouTube Channel Name") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Tv, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_channel_name_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = handle,
                    onValueChange = { handle = it },
                    label = { Text("Channel Handle (e.g. @MyChannel)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_channel_handle_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = token,
                    onValueChange = { token = it },
                    label = { Text("OAuth2 Access Token (Optional)") },
                    placeholder = { Text("ya29.a0AfH6...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Key, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_token_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val subText = when (channelName) {
                            "Tech & Hacks Shorts" -> "54.2K subscribers"
                            "Comedy Reels Daily" -> "120K subscribers"
                            else -> "12.4K subscribers"
                        }
                        onLoginSuccess(email, channelName, handle, subText, token)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0033)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_youtube_login_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sign In & Connect Channel",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
