package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.model.ScheduledShort
import com.example.util.TimeFormatUtil
import com.example.viewmodel.ShortsViewModel

@Composable
fun HistoryScreen(
    viewModel: ShortsViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val historyList by viewModel.publishedHistory.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                text = stringResource(R.string.title_history),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Live YouTube Shorts published from this device",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (historyList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(72.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.VideoLibrary,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "No Published Shorts Yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = stringResource(R.string.empty_history),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(historyList, key = { it.id }) { item ->
                    PublishedShortCard(
                        short = item,
                        onOpenYouTube = {
                            val targetUrl = item.youtubeVideoUrl ?: "https://youtube.com/shorts/${item.youtubeVideoId}"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl))
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Cannot open URL: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onCopyLink = {
                            val targetUrl = item.youtubeVideoUrl ?: "https://youtube.com/shorts/${item.youtubeVideoId}"
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("YouTube Short Link", targetUrl))
                            Toast.makeText(context, "Link copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        onDelete = {
                            viewModel.deleteShort(item)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PublishedShortCard(
    short: ScheduledShort,
    onOpenYouTube: () -> Unit,
    onCopyLink: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("history_card_${short.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 72.dp, height = 100.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    if (short.thumbnailPath != null) {
                        AsyncImage(
                            model = short.thumbnailPath,
                            contentDescription = "Thumbnail",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(28.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.VideoLibrary,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF06D6A0),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Published on YouTube",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF06D6A0)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = short.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Published ${TimeFormatUtil.formatShortDateTime(short.updatedAtEpochMs)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (short.youtubeVideoId != null) {
                        Text(
                            text = "ID: ${short.youtubeVideoId}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove record",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        onClick = onCopyLink,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy Link", style = MaterialTheme.typography.labelSmall)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFF0033),
                        modifier = Modifier
                            .clickable { onOpenYouTube() }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Open Short",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
