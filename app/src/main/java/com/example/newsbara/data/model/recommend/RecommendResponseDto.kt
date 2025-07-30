package com.example.newsbara.data.model.recommend

import com.google.gson.annotations.SerializedName

data class RecommendResponseDto(
    @SerializedName("recommendList")
    val recommendList: List<RecommendedVideoDto>
)

data class RecommendedVideoDto(
    @SerializedName("videoId")
    val videoId: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("thumbnail")
    val thumbnail: String,
    @SerializedName("length")
    val length: String,
    @SerializedName("channel")
    val channel: String,
    @SerializedName("category")
    val category: String
)