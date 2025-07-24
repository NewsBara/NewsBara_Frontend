package com.example.newsbara.data.model.friends

data class FriendSearchResponse(
    val userId: Int,
    val userName: String,
    val profileImage: String,
    val following: Boolean,
    val pending: Boolean,
    val sentByMe: Boolean,
    val receivedByMe: Boolean
)
