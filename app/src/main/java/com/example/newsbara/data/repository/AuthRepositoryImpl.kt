package com.example.newsbara.data.repository

import com.example.newsbara.data.model.login.LoginRequest
import com.example.newsbara.data.model.login.LoginResponse
import com.example.newsbara.data.model.signup.SignUpRequest
import com.example.newsbara.data.service.AuthService
import com.example.newsbara.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService
) : AuthRepository {

    override suspend fun signUp(request: SignUpRequest): Result<Unit> {
        return try {
            val response = authService.signUp(request)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("회원가입 실패: ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return try {
            val response = authService.login(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("로그인 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
