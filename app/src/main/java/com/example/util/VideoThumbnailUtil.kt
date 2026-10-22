package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.example.model.VideoMetadataInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat

object VideoThumbnailUtil {

    suspend fun extractThumbnailAndMetadata(
        context: Context,
        videoUri: Uri
    ): Pair<String?, VideoMetadataInfo> = withContext(Dispatchers.IO) {
        val retriever = MediaMetadataRetriever()
        var savedThumbnailPath: String? = null
        var metadataInfo = VideoMetadataInfo()

        try {
            retriever.setDataSource(context, videoUri)

            // Extract frame at 1 second (or start)
            val bitmap = retriever.getFrameAtTime(1_000_000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                ?: retriever.frameAtTime

            if (bitmap != null) {
                val thumbFile = File(context.cacheDir, "thumb_${System.currentTimeMillis()}.jpg")
                FileOutputStream(thumbFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
                }
                savedThumbnailPath = thumbFile.absolutePath
            }

            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val durationMs = durationStr?.toLongOrNull() ?: 0L
            val durationSeconds = durationMs / 1000

            val widthStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
            val heightStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
            val width = widthStr?.toIntOrNull() ?: 0
            val height = heightStr?.toIntOrNull() ?: 0

            var fileSizeFormatted = ""
            try {
                context.contentResolver.openFileDescriptor(videoUri, "r")?.use { pfd ->
                    val bytes = pfd.statSize
                    fileSizeFormatted = formatFileSize(bytes)
                }
            } catch (_: Exception) {
                fileSizeFormatted = "Unknown size"
            }

            metadataInfo = VideoMetadataInfo(
                durationSeconds = durationSeconds,
                width = width,
                height = height,
                fileSizeFormatted = fileSizeFormatted
            )
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                retriever.release()
            } catch (_: Exception) {}
        }

        Pair(savedThumbnailPath, metadataInfo)
    }

    private fun formatFileSize(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB")
        val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
        val df = DecimalFormat("#,##0.#")
        return "${df.format(bytes / Math.pow(1024.0, digitGroups.toDouble()))} ${units[digitGroups]}"
    }
}
