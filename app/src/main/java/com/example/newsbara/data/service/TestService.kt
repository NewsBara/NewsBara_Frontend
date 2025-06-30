package com.example.newsbara.data.service

import com.example.newsbara.data.BaseResponse
import com.example.newsbara.data.model.test.TestGenerationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface TestService {

    @GET("/api/tests/generate/{videoId}")
    suspend fun generateTest(
        @Path("videoId") videoId: String
    ): Response<BaseResponse<TestGenerationResponse>>
}
