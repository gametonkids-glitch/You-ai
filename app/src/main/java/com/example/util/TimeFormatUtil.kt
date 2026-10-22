package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object TimeFormatUtil {

    fun formatRemainingTime(epochMs: Long): String {
        val now = System.currentTimeMillis()
        val diffMs = epochMs - now

        if (diffMs <= 0) {
            return "Due now"
        }

        val minutes = diffMs / (60 * 1000)
        val hours = minutes / 60
        val days = hours / 24

        return when {
            minutes < 1 -> "In less than a minute"
            minutes < 60 -> "In $minutes min${if (minutes > 1) "s" else ""}"
            hours < 24 -> {
                val remainingMins = minutes % 60
                if (remainingMins == 0L) "In $hours hr${if (hours > 1) "s" else ""}"
                else "In $hours hr $remainingMins min"
            }
            days == 1L -> "Tomorrow (${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(epochMs))})"
            else -> "In $days days (${SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()).format(Date(epochMs))})"
        }
    }

    fun formatFullDateTime(epochMs: Long): String {
        val sdf = SimpleDateFormat("EEE, MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
        return sdf.format(Date(epochMs))
    }

    fun formatShortDateTime(epochMs: Long): String {
        val sdf = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
        return sdf.format(Date(epochMs))
    }

    /**
     * Converts epoch ms to RFC 3339 ISO-8601 UTC format required by YouTube Data API v3 publishAt
     * Example: "2026-10-22T18:30:00.000Z"
     */
    fun formatYoutubePublishAt(epochMs: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(epochMs))
    }

    fun getQuickPreset(presetType: PresetType): Long {
        val cal = Calendar.getInstance()
        when (presetType) {
            PresetType.PLUS_ONE_HOUR -> {
                cal.add(Calendar.HOUR_OF_DAY, 1)
            }
            PresetType.TONIGHT_8_PM -> {
                cal.set(Calendar.HOUR_OF_DAY, 20)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                if (cal.timeInMillis <= System.currentTimeMillis()) {
                    cal.add(Calendar.DAY_OF_YEAR, 1)
                }
            }
            PresetType.TOMORROW_12_PM -> {
                cal.add(Calendar.DAY_OF_YEAR, 1)
                cal.set(Calendar.HOUR_OF_DAY, 12)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
            }
            PresetType.TOMORROW_6_PM -> {
                cal.add(Calendar.DAY_OF_YEAR, 1)
                cal.set(Calendar.HOUR_OF_DAY, 18)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
            }
        }
        return cal.timeInMillis
    }

    enum class PresetType(val label: String) {
        PLUS_ONE_HOUR("+1 Hour"),
        TONIGHT_8_PM("Tonight 8 PM"),
        TOMORROW_12_PM("Tomorrow 12 PM"),
        TOMORROW_6_PM("Tomorrow 6 PM")
    }
}
