package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.model.UploadStatus
import com.example.ui.components.DateTimePickerUtil
import com.example.util.TimeFormatUtil
import com.example.viewmodel.ShortsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueScreen(
    viewModel: ShortsViewModel,
    onNavigateToCreate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val queueList by viewModel.activeQueue.collectAsState()
    val activeUploadingId by viewModel.activeUploadingId.collectAsState()
    val activeProgress by viewModel.activeUploadProgress.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("All (${queueList.size})", "Scheduled", "Uploading", "Drafts")

    val filteredList = when (selectedTabIndex) {
        1 -> queueList.filter { it.status == UploadStatus.SCHEDULED.name }
        2 -> queueList.filter { it.status == UploadStatus.UPLOADING.name || it.id == activeUploadingId }
        3 -> queueList.filter { it.status == UploadStatus.DRAFT.name }
        else -> queueList
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Header
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                text = stringResource(R.string.title_queue),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Track countdowns, live upload status, and scheduled releases",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            edgePadding = 16.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        if (filteredList.isEmpty()) {
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
                                imageVector = Icons.Default.HourglassTop,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Queue is empty",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = stringResource(R.string.empty_queue),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onNavigateToCreate,
                        modifier = Modifier.testTag("empty_queue_create_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Create a Short")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList, key = { it.id }) { item ->
                    val isCurrentlyUploading = item.id == activeUploadingId || item.status == UploadStatus.UPLOADING.name
                    val currentProgress = if (item.id == activeUploadingId) activeProgress else item.uploadProgressPercent

                    QueueItemCard(
                        short = item,
                        isUploading = isCurrentlyUploading,
                        progress = currentProgress,
                        onUploadNow = { viewModel.triggerUploadNow(item) },
                        onReschedule = {
                            DateTimePickerUtil.showDateTimePicker(
                                context = context,
                                initialEpochMs = item.scheduledTimeEpochMs
                            ) { newEpochMs ->
                                viewModel.setScheduledTime(newEpochMs)
                            }
                        },
                        onDelete = { viewModel.deleteShort(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun QueueItemCard(
    short: ScheduledShort,
    isUploading: Boolean,
    progress: Int,
    onUploadNow: () -> Unit,
    onReschedule: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("queue_card_${short.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Video thumbnail preview
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
                            imageVector = Icons.Default.PlayCircleFilled,
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
                    // Category & Status Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = short.categoryName,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Status Badge
                        val badgeBg = when (short.status) {
                            UploadStatus.UPLOADING.name -> Color(0xFF00B4D8)
                            UploadStatus.SCHEDULED.name -> Color(0xFFFFB703)
                            UploadStatus.FAILED.name -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.outline
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = badgeBg.copy(alpha = 0.15f),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isUploading) "Uploading ($progress%)" else short.status,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = badgeBg
                            )
                        }
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

                    // Timing Info
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = if (short.status == UploadStatus.SCHEDULED.name) Color(0xFFFFB703) else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (short.status == UploadStatus.SCHEDULED.name)
                                TimeFormatUtil.formatRemainingTime(short.scheduledTimeEpochMs)
                            else TimeFormatUtil.formatShortDateTime(short.scheduledTimeEpochMs),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (short.errorMessage != null && short.status == UploadStatus.FAILED.name) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = short.errorMessage,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Progress bar if uploading
            if (isUploading) {
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { progress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                if (short.status == UploadStatus.SCHEDULED.name || short.status == UploadStatus.DRAFT.name) {
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onReschedule,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditCalendar,
                            contentDescription = "Reschedule",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable(enabled = !isUploading) { onUploadNow() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Upload Now",
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
