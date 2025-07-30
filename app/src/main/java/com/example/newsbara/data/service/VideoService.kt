package com.example.newsbara.data.service

import com.example.newsbara.data.BaseResponse
import com.example.newsbara.data.model.script.ScriptResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface VideoService {

    @GET("/api/script/{videoId}")
    suspend fun getScriptByVideoId(
        @Path("videoId") videoId: String
    ): Response<BaseResponse<List<ScriptResponseDto>>>
}