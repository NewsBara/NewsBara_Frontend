package com.example.newsbara.data.service

import com.example.newsbara.data.model.mypage.BadgeInfo
import com.example.newsbara.data.BaseResponse
import com.example.newsbara.data.model.history.HistoryItem
import com.example.newsbara.data.model.mypage.MyPageInfo
import com.example.newsbara.data.model.mypage.PointRequest
import com.example.newsbara.data.model.mypage.PointResult
import com.example.newsbara.data.model.history.SaveHistoryRequest
import com.example.newsbara.data.model.mypage.UpdateNameRequest
import com.example.newsbara.data.model.mypage.UpdateNameResponse
import com.example.newsbara.data.model.mypage.UpdateProfileImageResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part


interface MyPageService {

    @GET("/api/mypage")
    suspend fun getMyPageInfo(): Response<BaseResponse<MyPageInfo>>

    @GET("/api/history")
    suspend fun getHistory(): Response<BaseResponse<List<HistoryItem>>>

    @POST("/api/history/save")
    suspend fun saveHistory(
        @Body request: SaveHistoryRequest
    ): Response<BaseResponse<HistoryItem>>

    @GET("/api/badge")
    suspend fun getBadge(): Response<BaseResponse<BadgeInfo>>

    @PUT("/api/user/point")
    suspend fun updatePoint(
        @Body request: PointRequest
    ): Response<BaseResponse<PointResult>>

    @PUT("/api/mypage/name")
    suspend fun updateName(
        @Body request: UpdateNameRequest
    ): Response<BaseResponse<UpdateNameResponse>>

    @Multipart
    @PUT("/api/mypage/profile")
    suspend fun updateProfileImage(
        @Part file: MultipartBody.Part
    ): Response<BaseResponse<UpdateProfileImageResponse>>

}
