package com.example.newsbara.data.service

import com.example.newsbara.data.BaseResponse
import com.example.newsbara.data.model.shadowing.PronunciationDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ShadowingService {
    @Multipart
    @POST("/api/shadowing/evaluate")
    suspend fun evaluateShadowing(
        @Query("script", encoded = true) script: String,
        @Part audio: MultipartBody.Part
    ): Response<BaseResponse<PronunciationDto>>

}