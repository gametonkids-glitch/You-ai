package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.viewmodel.ShortsViewModel

enum class ScreenTab {
    CREATE,
    QUEUE,
    PUBLISHED,
    SETTINGS
}

@Composable
fun MainScreen(
    viewModel: ShortsViewModel,
    modifier: Modifier = Modifier
) {
    var currentTab by remember { mutableStateOf(ScreenTab.CREATE) }

    val activeUploadingId by viewModel.activeUploadingId.collectAsState()
    val activeProgress by viewModel.activeUploadProgress.collectAsState()
    val bannerMessage by viewModel.statusBannerMessage.collectAsState()

    // Handle back button navigation
    BackHandler(enabled = currentTab != ScreenTab.CREATE) {
        currentTab = ScreenTab.CREATE
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            NavigationBar(
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = currentTab == ScreenTab.CREATE,
                    onClick = { currentTab = ScreenTab.CREATE },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == ScreenTab.CREATE) Icons.Filled.AddCircle else Icons.Outlined.AddCircleOutline,
                            contentDescription = stringResource(R.string.nav_create)
                        )
                    },
                    label = { Text(stringResource(R.string.nav_create)) },
                    modifier = Modifier.testTag("nav_item_create")
                )

                NavigationBarItem(
                    selected = currentTab == ScreenTab.QUEUE,
                    onClick = { currentTab = ScreenTab.QUEUE },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == ScreenTab.QUEUE) Icons.Filled.HourglassTop else Icons.Outlined.HourglassEmpty,
                            contentDescription = stringResource(R.string.nav_scheduled)
                        )
                    },
                    label = { Text(stringResource(R.string.nav_scheduled)) },
                    modifier = Modifier.testTag("nav_item_queue")
                )

                NavigationBarItem(
                    selected = currentTab == ScreenTab.PUBLISHED,
                    onClick = { currentTab = ScreenTab.PUBLISHED },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == ScreenTab.PUBLISHED) Icons.Filled.PlayCircle else Icons.Outlined.PlayCircleOutline,
                            contentDescription = stringResource(R.string.nav_published)
                        )
                    },
                    label = { Text(stringResource(R.string.nav_published)) },
                    modifier = Modifier.testTag("nav_item_published")
                )

                NavigationBarItem(
                    selected = currentTab == ScreenTab.SETTINGS,
                    onClick = { currentTab = ScreenTab.SETTINGS },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == ScreenTab.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                            contentDescription = stringResource(R.string.nav_settings)
                        )
                    },
                    label = { Text(stringResource(R.string.nav_settings)) },
                    modifier = Modifier.testTag("nav_item_settings")
                )
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Active Upload Banner
            AnimatedVisibility(
                visible = activeUploadingId != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { currentTab = ScreenTab.QUEUE }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CloudUpload,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Uploading Short to YouTube... $activeProgress%",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            Text(
                                text = "View Queue",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { activeProgress / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // General Notice / Status Banner
            AnimatedVisibility(
                visible = bannerMessage != null && activeUploadingId == null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                bannerMessage?.let { msg ->
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = msg,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }

                            IconButton(
                                onClick = { viewModel.dismissBannerMessage() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Content Screen Switcher
            Box(modifier = Modifier.weight(1f)) {
                when (currentTab) {
                    ScreenTab.CREATE -> CreateShortScreen(
                        viewModel = viewModel,
                        onNavigateToQueue = { currentTab = ScreenTab.QUEUE }
                    )
                    ScreenTab.QUEUE -> QueueScreen(
                        viewModel = viewModel,
                        onNavigateToCreate = { currentTab = ScreenTab.CREATE }
                    )
                    ScreenTab.PUBLISHED -> HistoryScreen(
                        viewModel = viewModel
                    )
                    ScreenTab.SETTINGS -> YouTubeSettingsScreen(
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
