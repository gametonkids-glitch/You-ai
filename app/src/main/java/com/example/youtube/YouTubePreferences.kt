package com.example.youtube

import android.content.Context
import android.content.SharedPreferences

class YouTubePreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("youtube_prefs", Context.MODE_PRIVATE)

    var isLoggedIn: Boolean
        get() = prefs.getBoolean("is_logged_in", true) // Default to true with default connected channel
        set(value) = prefs.edit().putBoolean("is_logged_in", value).apply()

    var userEmail: String
        get() = prefs.getString("user_email", "creator@youtube.com") ?: "creator@youtube.com"
        set(value) = prefs.edit().putString("user_email", value).apply()

    var accessToken: String
        get() = prefs.getString("access_token", "") ?: ""
        set(value) = prefs.edit().putString("access_token", value.trim()).apply()

    var channelTitle: String
        get() = prefs.getString("channel_title", "Shorts Studio Official") ?: "Shorts Studio Official"
        set(value) = prefs.edit().putString("channel_title", value).apply()

    var channelHandle: String
        get() = prefs.getString("channel_handle", "@ShortsStudioPro") ?: "@ShortsStudioPro"
        set(value) = prefs.edit().putString("channel_handle", value).apply()

    var channelSubscriberCount: String
        get() = prefs.getString("channel_subscriber_count", "12.4K subscribers") ?: "12.4K subscribers"
        set(value) = prefs.edit().putString("channel_subscriber_count", value).apply()

    var channelId: String
        get() = prefs.getString("channel_id", "UC_shorts_studio_channel") ?: "UC_shorts_studio_channel"
        set(value) = prefs.edit().putString("channel_id", value).apply()

    var isSimulatorMode: Boolean
        get() = prefs.getBoolean("is_simulator_mode", accessToken.isBlank())
        set(value) = prefs.edit().putBoolean("is_simulator_mode", value).apply()

    var defaultTags: String
        get() = prefs.getString("default_tags", "Shorts, YouTubeShorts, Viral, Trending") ?: ""
        set(value) = prefs.edit().putString("default_tags", value).apply()

    var defaultCategory: String
        get() = prefs.getString("default_category", "Entertainment") ?: "Entertainment"
        set(value) = prefs.edit().putString("default_category", value).apply()

    var defaultPrivacy: String
        get() = prefs.getString("default_privacy", "PUBLIC") ?: "PUBLIC"
        set(value) = prefs.edit().putString("default_privacy", value).apply()

    val isConnected: Boolean
        get() = isLoggedIn && (accessToken.isNotBlank() || isSimulatorMode)

    fun login(email: String, channelName: String, handle: String, subCount: String, token: String = "") {
        isLoggedIn = true
        userEmail = email
        channelTitle = channelName
        channelHandle = handle
        channelSubscriberCount = subCount
        if (token.isNotBlank()) {
            accessToken = token
            isSimulatorMode = false
        }
    }

    fun logout() {
        isLoggedIn = false
        accessToken = ""
        channelTitle = "Disconnected Channel"
        channelHandle = ""
        userEmail = ""
    }
}
