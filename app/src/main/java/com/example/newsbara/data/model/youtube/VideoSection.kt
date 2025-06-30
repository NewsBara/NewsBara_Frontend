package com.example.newsbara.data.model.youtube

import com.example.newsbara.data.model.history.HistoryItem

data class VideoSection(
    val categoryTitle: String,
    val videos: List<HistoryItem>
)
