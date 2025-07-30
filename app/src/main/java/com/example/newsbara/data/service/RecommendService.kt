package com.example.newsbara.data.service

import com.example.newsbara.data.BaseResponse
import com.example.newsbara.data.model.recommend.RecommendResponseDto
import retrofit2.Response
import retrofit2.http.GET

interface RecommendService {
    @GET("/api/recommend")
    suspend fun getRecommendedVideos(): Response<BaseResponse<RecommendResponseDto>>
}