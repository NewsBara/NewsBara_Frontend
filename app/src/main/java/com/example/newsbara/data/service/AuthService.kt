package com.example.newsbara.data.service

import com.example.newsbara.data.BaseResponse
import com.example.newsbara.data.model.login.LoginRequest
import com.example.newsbara.data.model.login.LoginResponse
import com.example.newsbara.data.model.signup.SignUpRequest
import com.example.newsbara.data.model.signup.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("/api/user/signup")
    suspend fun signUp(
        @Body request: SignUpRequest
    ): Response<BaseResponse<SignUpResponse>>

    @POST("/api/user/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<BaseResponse<LoginResponse>>

    @POST("/api/user/logout")
    suspend fun logout(): Response<BaseResponse<Unit>>

    @POST("/api/user/deleteUser")
    suspend fun deleteUser(): Response<BaseResponse<Unit>>
}
