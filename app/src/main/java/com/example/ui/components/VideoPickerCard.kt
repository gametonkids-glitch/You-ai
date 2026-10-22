package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.model.VideoMetadataInfo

@Composable
fun VideoPickerCard(
    selectedVideoUri: Uri?,
    thumbnailPath: String?,
    metadata: VideoMetadataInfo,
    isProcessing: Boolean,
    onVideoSelected: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onVideoSelected(uri)
        }
    }

    if (selectedVideoUri == null) {
        // Empty state / prompt to select
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                    )
                }
                .padding(24.dp)
                .testTag("select_video_button"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "Select Short Video",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.select_video),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.video_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        // Video Selected State
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Thumbnail preview box (9:16 aspect ratio preview)
                Box(
                    modifier = Modifier
                        .size(width = 80.dp, height = 120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    if (thumbnailPath != null) {
                        AsyncImage(
                            model = thumbnailPath,
                            contentDescription = "Short video preview thumbnail",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )
                        Icon(
                            imageVector = Icons.Default.PlayCircleFilled,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(32.dp)
                        )
                    } else if (isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF06D6A0),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Short Video Ready",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF06D6A0)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    if (metadata.durationSeconds > 0) {
                        Text(
                            text = "Duration: ${metadata.durationSeconds}s",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (metadata.fileSizeFormatted.isNotBlank()) {
                        Text(
                            text = "Size: ${metadata.fileSizeFormatted}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (metadata.width > 0 && metadata.height > 0) {
                        Text(
                            text = "Resolution: ${metadata.width}x${metadata.height}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                            )
                        },
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("change_video_button"),
                        contentPadding = ButtonDefaults.TextButtonContentPadding
                    ) {
                        Text(
                            text = stringResource(R.string.change_video),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}
