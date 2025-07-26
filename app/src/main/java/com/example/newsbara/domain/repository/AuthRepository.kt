package com.example.newsbara.domain.repository

import com.example.newsbara.data.model.login.LoginRequest
import com.example.newsbara.data.model.login.LoginResponse
import com.example.newsbara.data.model.signup.SignUpRequest
import com.example.newsbara.presentation.util.ResultState
import retrofit2.Response

interface AuthRepository {
    suspend fun signUp(request: SignUpRequest): ResultState<Unit>
    suspend fun login(request: LoginRequest): ResultState<LoginResponse>
    suspend fun logout(): ResultState<Unit>
    suspend fun deleteUser(): ResultState<Unit>
}