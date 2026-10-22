package com.example.model

enum class UploadStatus {
    DRAFT,
    SCHEDULED,
    UPLOADING,
    PUBLISHED,
    FAILED
}

enum class PrivacyStatus(val value: String, val displayName: String) {
    PUBLIC("public", "Public"),
    UNLISTED("unlisted", "Unlisted"),
    PRIVATE("private", "Private")
}

data class VideoMetadataInfo(
    val durationSeconds: Long = 0,
    val width: Int = 0,
    val height: Int = 0,
    val fileSizeFormatted: String = ""
)
