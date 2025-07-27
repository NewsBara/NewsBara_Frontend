package com.example.newsbara.data.service

import com.example.newsbara.data.model.youtube.VideoCategoryResponse
import com.example.newsbara.data.model.youtube.YouTubeSearchResponse
import com.example.newsbara.data.model.youtube.YouTubeVideoDetailsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeApiService {
    @GET("search")
    suspend fun searchVideosByChannel(
        @Query("part") part: String = "snippet",
        @Query("channelId") channelId: String,
        @Query("q") query: String,
        @Query("type") type: String = "video",
        @Query("maxResults") maxResults: Int = 10,
        @Query("key") apiKey: String
    ): YouTubeSearchResponse

    @GET("videos")
    suspend fun getVideoDetails(
        @Query("part") part: String = "contentDetails",
        @Query("id") videoIds: String, // 여러 개면 콤마(,) 구분
        @Query("key") apiKey: String
    ): YouTubeVideoDetailsResponse

    @GET("videoCategories")
    suspend fun getVideoCategories(
        @Query("part") part: String = "snippet",
        @Query("regionCode") regionCode: String = "US",
        @Query("key") apiKey: String
    ): VideoCategoryResponse


}