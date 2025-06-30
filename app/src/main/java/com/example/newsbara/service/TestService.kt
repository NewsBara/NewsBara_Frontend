package com.example.newsbara.service

import com.example.newsbara.data.BaseResponse
import com.example.newsbara.data.TestGenerationResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface TestService {

    @GET("/api/tests/generate/{videoId}")
    suspend fun generateTest(
        @Path("videoId") videoId: String
    ): BaseResponse<TestGenerationResponse>
}
