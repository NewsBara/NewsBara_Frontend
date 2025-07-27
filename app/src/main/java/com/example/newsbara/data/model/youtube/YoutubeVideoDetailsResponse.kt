package com.example.newsbara.data.model.youtube

data class YouTubeVideoDetailsResponse(
    val items: List<VideoDetailsItem>
)

data class VideoDetailsItem(
    val id: String,
    val contentDetails: ContentDetails,
    val snippet: CategorySnippet
)

data class ContentDetails(
    val duration: String
)

data class CategorySnippet(
    val categoryId: String
)
