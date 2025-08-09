package com.example.newsbara.data.service

import com.example.newsbara.data.BaseResponse
import com.example.newsbara.data.model.recommend.RecommendResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RecommendService {
    @GET("/api/recommend/{channelName}")
    suspend fun getRecommendedVideos(
        @Path("channelName", encoded = true) channelName: String
    ): Response<BaseResponse<RecommendResponseDto>>
}