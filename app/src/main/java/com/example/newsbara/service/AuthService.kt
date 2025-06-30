package com.example.newsbara.service

import com.example.newsbara.data.BaseResponse
import com.example.newsbara.data.LoginRequest
import com.example.newsbara.data.LoginResponse
import com.example.newsbara.data.SignUpRequest
import com.example.newsbara.data.SignUpResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("/api/user/signup")
    suspend fun signUp(
        @Body request: SignUpRequest
    ): BaseResponse<SignUpResponse>

    @POST("/api/user/login")
    suspend fun login(
        @Body request: LoginRequest
    ): BaseResponse<LoginResponse>

    @POST("/api/user/logout")
    suspend fun logout(): BaseResponse<Unit>


}
