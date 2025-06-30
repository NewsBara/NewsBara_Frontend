package com.example.newsbara.data.service

import com.example.newsbara.data.model.youtube.YouTubeSearchResponse
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
}