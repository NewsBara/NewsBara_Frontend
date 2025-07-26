package com.example.newsbara.data.repository

import com.example.newsbara.data.model.login.LoginRequest
import com.example.newsbara.data.model.login.LoginResponse
import com.example.newsbara.data.model.signup.SignUpRequest
import com.example.newsbara.data.service.AuthService
import com.example.newsbara.domain.repository.AuthRepository
import com.example.newsbara.presentation.util.ResultState
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService
) : AuthRepository {

    override suspend fun signUp(request: SignUpRequest): ResultState<Unit> {
        return try {
            val response = authService.signUp(request)
            if (response.isSuccessful) {
                ResultState.Success(Unit)
            } else {
                ResultState.Failure("회원가입 실패: ${response.code()}")
            }
        } catch (e: Exception) {
            ResultState.Failure("예외 발생: ${e.localizedMessage ?: "알 수 없음"}")
        }
    }

    override suspend fun login(request: LoginRequest): ResultState<LoginResponse> {
        return try {
            val response = authService.login(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.isSuccess) {
                    ResultState.Success(body.result)
                } else {
                    ResultState.Failure("서버 응답 실패: ${body?.message ?: "알 수 없음"}")
                }
            } else {
                ResultState.Failure("HTTP 오류: ${response.code()}")
            }
        } catch (e: Exception) {
            ResultState.Failure("예외 발생: ${e.localizedMessage ?: "알 수 없음"}")
        }
    }

    override suspend fun logout(): ResultState<Unit> {
        return try {
            val response = authService.logout()
            if (response.isSuccessful) {
                ResultState.Success(Unit)
            } else {
                ResultState.Failure("로그아웃 실패: ${response.code()}")
            }
        } catch (e: Exception) {
            ResultState.Failure("예외 발생: ${e.localizedMessage ?: "알 수 없음"}")
        }
    }

    override suspend fun deleteUser(): ResultState<Unit> {
        return try {
            val response = authService.deleteUser()
            if (response.isSuccessful) {
                ResultState.Success(Unit)
            } else {
                ResultState.Failure("회원탈퇴 실패: ${response.code()}")
            }
        } catch (e: Exception) {
            ResultState.Failure("예외 발생: ${e.localizedMessage ?: "알 수 없음"}")
        }
    }
}
