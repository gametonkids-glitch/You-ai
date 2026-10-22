package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scheduled_shorts")
data class ScheduledShort(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val videoUri: String,
    val thumbnailPath: String? = null,
    val title: String,
    val caption: String,
    val description: String,
    val tags: String = "",
    val categoryId: String = "24", // 24 = Entertainment
    val categoryName: String = "Entertainment",
    val privacyStatus: String = "PUBLIC",
    val scheduledTimeEpochMs: Long,
    val isPublishImmediately: Boolean = false,
    val status: String = UploadStatus.SCHEDULED.name,
    val uploadProgressPercent: Int = 0,
    val youtubeVideoId: String? = null,
    val youtubeVideoUrl: String? = null,
    val errorMessage: String? = null,
    val durationSeconds: Long = 0,
    val createdAtEpochMs: Long = System.currentTimeMillis(),
    val updatedAtEpochMs: Long = System.currentTimeMillis()
)
