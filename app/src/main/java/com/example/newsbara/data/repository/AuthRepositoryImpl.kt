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
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.isSuccess) {
                    Result.success(body.result)
                } else {
                    Result.failure(Exception("서버 응답 실패: ${body?.message ?: "알 수 없음"}"))
                }
            } else {
                Result.failure(Exception("HTTP 오류: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            val response = authService.logout()
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("로그아웃 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
