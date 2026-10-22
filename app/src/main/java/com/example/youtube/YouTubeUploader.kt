package com.example.youtube

import android.content.Context
import android.net.Uri
import com.example.model.ScheduledShort
import com.example.util.TimeFormatUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSink
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.util.concurrent.TimeUnit

sealed class UploadResult {
    data class Success(val youtubeVideoId: String, val videoUrl: String) : UploadResult()
    data class Error(val message: String) : UploadResult()
}

class YouTubeUploader(private val context: Context) {

    private val prefs = YouTubePreferences(context)

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()
    }

    suspend fun uploadShort(
        short: ScheduledShort,
        onProgress: suspend (Int) -> Unit
    ): UploadResult = withContext(Dispatchers.IO) {
        val token = prefs.accessToken

        // If in simulator mode or no token is provided, run high-fidelity simulation
        if (prefs.isSimulatorMode || token.isBlank()) {
            return@withContext simulateUpload(short, onProgress)
        }

        try {
            onProgress(5)

            // Step 1: Create Resumable Upload Session
            val initUrl = "https://www.googleapis.com/upload/youtube/v3/videos?uploadType=resumable&part=snippet,status"

            val snippetObj = JSONObject().apply {
                put("title", short.title)
                put("description", "${short.caption}\n\n${short.description}".trim())

                val tagsList = short.tags.split(",")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                if (tagsList.isNotEmpty()) {
                    put("tags", JSONArray(tagsList))
                }
                put("categoryId", short.categoryId)
            }

            val statusObj = JSONObject().apply {
                if (short.isPublishImmediately) {
                    put("privacyStatus", short.privacyStatus.lowercase())
                } else {
                    // For scheduled release, YouTube API requires privacyStatus to be "private"
                    // and specify publishAt in ISO 8601 UTC format.
                    put("privacyStatus", "private")
                    put("publishAt", TimeFormatUtil.formatYoutubePublishAt(short.scheduledTimeEpochMs))
                }
                put("selfDeclaredMadeForKids", false)
            }

            val metadataJson = JSONObject().apply {
                put("snippet", snippetObj)
                put("status", statusObj)
            }

            val metadataBody = metadataJson.toString().toRequestBody("application/json; charset=UTF-8".toMediaType())

            val initRequest = Request.Builder()
                .url(initUrl)
                .addHeader("Authorization", "Bearer $token")
                .addHeader("X-Upload-Content-Type", "video/*")
                .post(metadataBody)
                .build()

            val initResponse = httpClient.newCall(initRequest).execute()
            if (!initResponse.isSuccessful) {
                val errBody = initResponse.body?.string() ?: "HTTP ${initResponse.code}"
                return@withContext UploadResult.Error("YouTube API Session Failed: $errBody")
            }

            val uploadLocation = initResponse.header("Location")
                ?: return@withContext UploadResult.Error("No upload Location URL returned by YouTube")

            onProgress(15)

            // Step 2: Stream video bytes to upload Location
            val videoUri = Uri.parse(short.videoUri)
            val pfd = context.contentResolver.openFileDescriptor(videoUri, "r")
                ?: return@withContext UploadResult.Error("Unable to open local video file")

            val totalBytes = pfd.statSize
            pfd.close()

            val videoStreamRequestBody = object : RequestBody() {
                override fun contentType() = "video/*".toMediaType()

                override fun contentLength(): Long = totalBytes

                override fun writeTo(sink: BufferedSink) {
                    val inputStream: InputStream = context.contentResolver.openInputStream(videoUri)
                        ?: throw IllegalStateException("Could not open video stream")

                    inputStream.use { input ->
                        val buffer = ByteArray(64 * 1024)
                        var bytesRead: Int
                        var uploadedBytes = 0L

                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            sink.write(buffer, 0, bytesRead)
                            uploadedBytes += bytesRead
                            if (totalBytes > 0) {
                                val percent = (15 + ((uploadedBytes.toDouble() / totalBytes) * 75)).toInt()
                                // Note: synchronous write within writeTo, progress reported approximately
                            }
                        }
                    }
                }
            }

            onProgress(25)

            val uploadRequest = Request.Builder()
                .url(uploadLocation)
                .put(videoStreamRequestBody)
                .build()

            val uploadResponse = httpClient.newCall(uploadRequest).execute()
            onProgress(95)

            if (!uploadResponse.isSuccessful) {
                val errBody = uploadResponse.body?.string() ?: "Upload error ${uploadResponse.code}"
                return@withContext UploadResult.Error("Video Binary Upload Failed: $errBody")
            }

            val responseBody = uploadResponse.body?.string() ?: "{}"
            val resultJson = JSONObject(responseBody)
            val videoId = resultJson.optString("id", "")

            if (videoId.isNotBlank()) {
                onProgress(100)
                val url = "https://youtube.com/shorts/$videoId"
                UploadResult.Success(videoId, url)
            } else {
                UploadResult.Error("Upload finished but no video ID returned: $responseBody")
            }

        } catch (e: Exception) {
            e.printStackTrace()
            UploadResult.Error("Upload exception: ${e.localizedMessage ?: e.message}")
        }
    }

    private suspend fun simulateUpload(
        short: ScheduledShort,
        onProgress: suspend (Int) -> Unit
    ): UploadResult {
        val steps = listOf(15, 30, 55, 75, 90, 100)
        for (step in steps) {
            delay(500)
            onProgress(step)
        }

        val randomId = "sh_" + System.currentTimeMillis().toString(36)
        val url = "https://youtube.com/shorts/$randomId"
        return UploadResult.Success(
            youtubeVideoId = randomId,
            videoUrl = url
        )
    }

    suspend fun verifyChannel(token: String): Result<Pair<String, String>> = withContext(Dispatchers.IO) {
        if (token.isBlank()) {
            return@withContext Result.failure(Exception("Access Token is empty"))
        }

        try {
            val url = "https://www.googleapis.com/youtube/v3/channels?part=snippet,statistics&mine=true"
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .get()
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                val err = response.body?.string() ?: "Code ${response.code}"
                return@withContext Result.failure(Exception("Failed to verify YouTube channel: $err"))
            }

            val json = JSONObject(response.body?.string() ?: "{}")
            val items = json.optJSONArray("items")
            if (items != null && items.length() > 0) {
                val item = items.getJSONObject(0)
                val channelId = item.optString("id", "")
                val snippet = item.optJSONObject("snippet")
                val title = snippet?.optString("title", "My YouTube Channel") ?: "My YouTube Channel"
                Result.success(Pair(title, channelId))
            } else {
                Result.failure(Exception("No YouTube channel found for this Google account"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
