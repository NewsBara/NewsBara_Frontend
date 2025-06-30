package com.example.newsbara.data.model.history

data class HistoryItem(
    val id: Int,
    val videoId: String,
    val title: String,
    val thumbnail: String,
    val channel: String,
    val length: String,
    val category: String,
    val status: String,
    val createdAt: String
)