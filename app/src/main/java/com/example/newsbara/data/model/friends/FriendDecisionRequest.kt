package com.example.newsbara.data.model.friends

import com.google.gson.annotations.SerializedName

data class FriendDecisionRequest(
    @SerializedName("followStatus")
    val followStatus: String
)
