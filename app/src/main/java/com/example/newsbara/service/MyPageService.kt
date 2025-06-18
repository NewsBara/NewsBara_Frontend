package com.example.newsbara.service

import com.example.newsbara.data.BadgeInfo
import com.example.newsbara.data.BaseResponse
import com.example.newsbara.data.HistoryItem
import com.example.newsbara.data.SaveHistoryRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface MyPageService {

    @GET("/api/history")
    suspend fun getHistory(): BaseResponse<List<HistoryItem>>

    @POST("/api/history/save")
    suspend fun saveHistory(
        @Body request: SaveHistoryRequest
    ): BaseResponse<HistoryItem>

    @GET("/api/badge")
    suspend fun getBadge(): BaseResponse<BadgeInfo>
}