package com.example.newsbara.di


object YouTubeUtil {
    fun parseYouTubeDuration(duration: String): String {
        val d = java.time.Duration.parse(duration)
        val minutes = d.toMinutes()
        val seconds = d.seconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}
