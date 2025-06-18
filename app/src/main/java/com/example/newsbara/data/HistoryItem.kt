package com.example.newsbara.data

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

// 히스토리 저장 요청 객체
data class SaveHistoryRequest(
    val videoId: String,
    val title: String,
    val thumbnail: String,
    val channel: String,
    val length: String,
    val category: String,
    val status: String
)