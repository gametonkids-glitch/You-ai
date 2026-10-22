package com.example.ai

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiShortsGenerator {

    private const val TAG = "GeminiShortsGen"
    private const val MODEL_NAME = "gemini-3.5-flash"

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    suspend fun autoGenerateMetadata(
        videoHint: String = "",
        category: String = "Entertainment"
    ): AiGeneratedShortPlan = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = """
                    Generate viral, high-converting metadata for a YouTube Shorts vertical video.
                    Topic or context hint: ${if (videoHint.isNotBlank()) videoHint else "Trending viral short entertainment, quick life tips, or interesting facts"}
                    Category: $category
                    
                    Respond strictly in valid JSON with these exact keys:
                    {
                      "title": "A short, catchy, clickable title under 90 characters including #Shorts and relevant emoji",
                      "caption": "A 1-2 sentence engaging hook caption that asks a question or creates curiosity",
                      "description": "Full YouTube Shorts description with hook, detailed summary, call to subscribe, and 4-6 viral hashtags like #Shorts #Viral #Trending",
                      "tags": "Comma-separated list of 6-8 relevant search tags",
                      "recommendedTime": "Best time of day to post this content"
                    }
                """.trimIndent()

                val requestJson = JSONObject().apply {
                    val contentsArr = JSONArray().apply {
                        val contentObj = JSONObject().apply {
                            val partsArr = JSONArray().apply {
                                put(JSONObject().put("text", prompt))
                            }
                            put("parts", partsArr)
                        }
                        put(contentObj)
                    }
                    put("contents", contentsArr)

                    val genConfig = JSONObject().apply {
                        put("responseMimeType", "application/json")
                        put("temperature", 0.7)
                    }
                    put("generationConfig", genConfig)
                }

                val url = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent?key=$apiKey"
                val body = requestJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
                val request = Request.Builder().url(url).post(body).build()

                val response = httpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    val resStr = response.body?.string() ?: ""
                    val rootJson = JSONObject(resStr)
                    val candidates = rootJson.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val candidate = candidates.getJSONObject(0)
                        val content = candidate.optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        val text = parts?.optJSONObject(0)?.optString("text", "") ?: ""

                        if (text.isNotBlank()) {
                            val parsed = JSONObject(text)
                            return@withContext AiGeneratedShortPlan(
                                title = parsed.optString("title", "Viral Short Video #Shorts"),
                                caption = parsed.optString("caption", "Check this out! #Shorts"),
                                description = parsed.optString("description", "Watch till the end! #Shorts #Viral"),
                                tags = parsed.optString("tags", "Shorts, Viral, Trending, YouTubeShorts"),
                                recommendedTimeNotice = parsed.optString("recommendedTime", "Best time to post: 6:00 PM - 8:00 PM")
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Gemini API call failed, falling back to local generator: ${e.message}")
            }
        }

        // Seamless high-quality local generator fallback
        ShortsAiEnhancer.generatePlan(videoHint, category)
    }
}
