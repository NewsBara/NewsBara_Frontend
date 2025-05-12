package com.example.newsbara.retrofit

import com.example.newsbara.data.YouTubeSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeApiService {
    @GET("search")
    suspend fun searchVideosByChannel(
        @Query("part") part: String = "snippet",
        @Query("channelId") channelId: String,            // ✅ 추가
        @Query("q") query: String,                        // 카테고리: "Science", "Politics" 등
        @Query("type") type: String = "video",
        @Query("maxResults") maxResults: Int = 10,
        @Query("key") apiKey: String
    ): YouTubeSearchResponse

}
