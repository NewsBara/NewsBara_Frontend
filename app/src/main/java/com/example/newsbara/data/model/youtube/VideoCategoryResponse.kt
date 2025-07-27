package com.example.newsbara.data.model.youtube

data class VideoCategoryResponse(
    val items: List<VideoCategoryItem>
)

data class VideoCategoryItem(
    val id: String,  // categoryId
    val snippet: VideoCategorySnippet
)

data class VideoCategorySnippet(
    val title: String
)