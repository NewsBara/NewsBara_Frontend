package com.example.newsbara.data.model.friends

data class FriendRequestItem(
    val id: Int,
    val followerId: Int,
    val followerName: String,
    val followerPoint: Int,
    val followerProfileImage: String,
    val followStatus: String
)
