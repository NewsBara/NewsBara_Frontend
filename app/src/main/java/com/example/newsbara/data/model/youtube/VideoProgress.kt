package com.example.newsbara.data.model.youtube

data class VideoProgress(
    val videoId: String,
    val title: String,
    val thumbnailUrl: String,
    val completedStep: Step
)

enum class Step {
    VIDEO, SHADOWING, TEST, DICTIONARY
}
