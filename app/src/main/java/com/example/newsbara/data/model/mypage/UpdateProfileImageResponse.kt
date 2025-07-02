package com.example.newsbara.data.model.mypage

import com.google.gson.annotations.SerializedName

data class UpdateProfileImageResponse(
    val id: Int,
    @SerializedName("profile_url")
    val profileUrl: String
)
