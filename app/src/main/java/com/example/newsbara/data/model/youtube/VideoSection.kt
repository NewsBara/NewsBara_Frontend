package com.example.newsbara.data.model.youtube

import com.example.newsbara.data.model.history.HistoryItem

data class VideoSection(
    val channelName: String,
    val videos: List<HistoryItem>
)
