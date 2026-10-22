package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
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
import com.example.model.PrivacyStatus
import com.example.ui.components.AiEnhancerDialog
import com.example.ui.components.DateTimePickerUtil
import com.example.ui.components.VideoPickerCard
import com.example.ui.components.YouTubeLoginDialog
import com.example.util.TimeFormatUtil
import com.example.viewmodel.ShortsViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateShortScreen(
    viewModel: ShortsViewModel,
    onNavigateToQueue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val selectedVideoUri by viewModel.selectedVideoUri.collectAsState()
    val thumbnailPath by viewModel.videoThumbnailPath.collectAsState()
    val metadata by viewModel.videoMetadata.collectAsState()
    val isProcessingVideo by viewModel.isProcessingVideo.collectAsState()
    val isGeneratingAi by viewModel.isGeneratingAi.collectAsState()

    val title by viewModel.title.collectAsState()
    val caption by viewModel.caption.collectAsState()
    val description by viewModel.description.collectAsState()
    val tags by viewModel.tags.collectAsState()
    val categoryId by viewModel.categoryId.collectAsState()
    val categoryName by viewModel.categoryName.collectAsState()
    val privacyStatus by viewModel.privacyStatus.collectAsState()
    val isPublishImmediately by viewModel.isPublishImmediately.collectAsState()
    val scheduledEpochMs by viewModel.scheduledEpochMs.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()

    // YouTube connection states
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val channelTitle by viewModel.channelTitle.collectAsState()
    val channelHandle by viewModel.channelHandle.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()

    var showAiDialog by remember { mutableStateOf(false) }
    var showLoginDialog by remember { mutableStateOf(false) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    val categories = listOf(
        Pair("24", "Entertainment"),
        Pair("20", "Gaming"),
        Pair("28", "Science & Technology"),
        Pair("23", "Comedy"),
        Pair("10", "Music"),
        Pair("26", "Howto & Style"),
        Pair("27", "Education"),
        Pair("17", "Sports"),
        Pair("22", "People & Blogs")
    )

    if (showAiDialog) {
        AiEnhancerDialog(
            currentCategory = categoryName,
            onDismiss = { showAiDialog = false },
            onApplyPlan = { plan ->
                viewModel.applyAiPlan(plan)
            }
        )
    }

    if (showLoginDialog) {
        YouTubeLoginDialog(
            currentEmail = userEmail,
            currentChannelTitle = channelTitle,
            currentHandle = channelHandle,
            onDismiss = { showLoginDialog = false },
            onLoginSuccess = { email, name, handle, subs, token ->
                viewModel.loginChannel(email, name, handle, subs, token)
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Connected YouTube Channel Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showLoginDialog = true }
                .testTag("destination_channel_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFF0033),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Uploading to: ",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = channelTitle,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (isLoggedIn) Color(0xFF06D6A0) else MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isLoggedIn) "$channelHandle • Connected" else "Not Connected • Tap to Login",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isLoggedIn) Color(0xFF06D6A0) else MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isLoggedIn) "Switch" else "Login",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(R.string.header_create_short),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Pick video — Title, Description & Tags auto-fill via AI!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { showAiDialog = true }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .testTag("open_ai_assistant_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Assistant",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "AI Custom",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Video Picker Card
        VideoPickerCard(
            selectedVideoUri = selectedVideoUri,
            thumbnailPath = thumbnailPath,
            metadata = metadata,
            isProcessing = isProcessingVideo,
            onVideoSelected = { viewModel.onVideoSelected(it) }
        )

        // AI Auto-Generating Indicator Banner
        AnimatedVisibility(
            visible = isGeneratingAi,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "✨ AI is automatically generating Title, Description & Tags...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Title Input Section with AI regenerate action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.label_title),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                // AI Auto-Fill action button
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    modifier = Modifier
                        .clickable { viewModel.autoGenerateAiMetadata() }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Auto AI Fill",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // One-tap "+ #Shorts" chip
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clickable { viewModel.appendShortsHashtag() }
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "+ #Shorts",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "${title.length}/100",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (title.length > 100) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { if (it.length <= 100) viewModel.updateTitle(it) },
            placeholder = { Text(stringResource(R.string.placeholder_title)) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("short_title_input"),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Caption & Description Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.label_caption),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .clickable {
                        viewModel.updateCaption(caption + "\n\n#Shorts #YouTubeShorts #Viral #Trending")
                    }
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "+ Viral Tags",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = caption,
            onValueChange = { viewModel.updateCaption(it) },
            placeholder = { Text(stringResource(R.string.placeholder_caption)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .testTag("short_caption_input"),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tags Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Tag,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.label_tags),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Auto-generated by AI",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF06D6A0),
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = tags,
            onValueChange = { viewModel.updateTags(it) },
            placeholder = { Text(stringResource(R.string.placeholder_tags)) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("short_tags_input"),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Category Selector
        Text(
            text = stringResource(R.string.label_category),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        ExposedDropdownMenuBox(
            expanded = categoryDropdownExpanded,
            onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
        ) {
            OutlinedTextField(
                value = categoryName,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            ExposedDropdownMenu(
                expanded = categoryDropdownExpanded,
                onDismissRequest = { categoryDropdownExpanded = false }
            ) {
                categories.forEach { (catId, catName) ->
                    DropdownMenuItem(
                        text = { Text(catName) },
                        onClick = {
                            viewModel.updateCategory(catId, catName)
                            categoryDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Timing & Scheduling Card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.label_timing),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Radio Options: Publish Immediately vs Schedule for Later
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setPublishImmediately(false) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = !isPublishImmediately,
                        onClick = { viewModel.setPublishImmediately(false) },
                        modifier = Modifier.testTag("radio_schedule_later")
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.option_schedule_later),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (!isPublishImmediately) FontWeight.Bold else FontWeight.Normal
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setPublishImmediately(true) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isPublishImmediately,
                        onClick = { viewModel.setPublishImmediately(true) },
                        modifier = Modifier.testTag("radio_publish_now")
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.option_publish_now),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isPublishImmediately) FontWeight.Bold else FontWeight.Normal
                    )
                }

                // If scheduled for later, show Quick Presets & Picker
                if (!isPublishImmediately) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Quick Presets:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TimeFormatUtil.PresetType.values().forEach { preset ->
                            val targetMs = TimeFormatUtil.getQuickPreset(preset)
                            val isSelected = Math.abs(scheduledEpochMs - targetMs) < 60_000
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surface,
                                modifier = Modifier
                                    .clickable { viewModel.setScheduledTime(targetMs) }
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = preset.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Date & Time Custom Picker button
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                DateTimePickerUtil.showDateTimePicker(
                                    context = context,
                                    initialEpochMs = scheduledEpochMs,
                                    onDateTimeSelected = { selectedMs ->
                                        viewModel.setScheduledTime(selectedMs)
                                    }
                                )
                            }
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp)
                            .testTag("custom_datetime_picker_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Scheduled Release Time",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = TimeFormatUtil.formatFullDateTime(scheduledEpochMs),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Text(
                                text = "Edit",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Countdown: ${TimeFormatUtil.formatRemainingTime(scheduledEpochMs)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFFB703),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Privacy Setting
        Text(
            text = stringResource(R.string.privacy_setting),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PrivacyStatus.values().forEach { privacy ->
                val isSelected = privacyStatus == privacy
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.updatePrivacy(privacy) }
                        .padding(vertical = 10.dp),
                    tonalElevation = if (isSelected) 2.dp else 0.dp
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = when (privacy) {
                                PrivacyStatus.PUBLIC -> Icons.Default.Public
                                PrivacyStatus.UNLISTED -> Icons.Default.Visibility
                                PrivacyStatus.PRIVATE -> Icons.Default.Lock
                            },
                            contentDescription = null,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = privacy.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Action Buttons: Schedule/Upload Now & Save Draft
        Button(
            onClick = {
                viewModel.scheduleOrUpload {
                    onNavigateToQueue()
                }
            },
            enabled = !isSubmitting,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("schedule_upload_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = if (isPublishImmediately) Icons.Default.CloudUpload else Icons.Default.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isPublishImmediately) "Upload to $channelTitle" else stringResource(R.string.btn_schedule),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = {
                viewModel.saveDraft {
                    onNavigateToQueue()
                }
            },
            enabled = !isSubmitting,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("save_draft_button"),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.btn_save_draft),
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
