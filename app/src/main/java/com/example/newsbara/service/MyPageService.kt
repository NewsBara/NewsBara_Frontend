package com.example.newsbara.service

import com.example.newsbara.data.BadgeInfo
import com.example.newsbara.data.BaseResponse
import com.example.newsbara.data.HistoryItem
import com.example.newsbara.data.MyPageInfo
import com.example.newsbara.data.PointRequest
import com.example.newsbara.data.PointResult
import com.example.newsbara.data.SaveHistoryRequest
import com.example.newsbara.data.UpdateNameRequest
import com.example.newsbara.data.UpdateNameResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT


interface MyPageService {

    @GET("/api/mypage")
    suspend fun getMyPageInfo(): BaseResponse<MyPageInfo>

    @GET("/api/history")
    suspend fun getHistory(): BaseResponse<List<HistoryItem>>

    @POST("/api/history/save")
    suspend fun saveHistory(
        @Body request: SaveHistoryRequest
    ): BaseResponse<HistoryItem>

    @GET("/api/badge")
    suspend fun getBadge(): BaseResponse<BadgeInfo>

    @PUT("/api/user/point")
    suspend fun updatePoint(
        @Body request: PointRequest
    ): BaseResponse<PointResult>

    @PUT("/api/mypage/name")
    suspend fun updateName(
        @Body request: UpdateNameRequest
    ): BaseResponse<UpdateNameResponse>

}