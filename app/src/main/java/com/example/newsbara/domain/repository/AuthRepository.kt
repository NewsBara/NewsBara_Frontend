package com.example.newsbara.domain.repository

import com.example.newsbara.data.model.login.LoginRequest
import com.example.newsbara.data.model.login.LoginResponse
import com.example.newsbara.data.model.signup.SignUpRequest
import retrofit2.Response

interface AuthRepository {
    suspend fun signUp(request: SignUpRequest): Result<Unit>
    suspend fun login(request: LoginRequest): Result<LoginResponse>
}