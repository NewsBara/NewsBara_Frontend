package com.example.newsbara.data.repository

import com.example.newsbara.data.BaseResponse
import com.example.newsbara.data.model.test.TestGenerationResponse
import com.example.newsbara.data.service.TestService
import com.example.newsbara.domain.repository.TestRepository
import com.example.newsbara.presentation.util.ResultState
import javax.inject.Inject

class TestRepositoryImpl @Inject constructor(
    private val testService: TestService
) : TestRepository {

    override suspend fun generateTest(videoId: String): ResultState<TestGenerationResponse> {
        return try {
            val response = testService.generateTest(videoId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.isSuccess == true) {
                    ResultState.Success(body.result)
                } else {
                    ResultState.Failure(body?.message ?: "API 실패")
                }
            } else {
                ResultState.Failure("HTTP 오류: ${response.code()}")
            }
        } catch (e: Exception) {
            ResultState.Failure(e.message ?: "네트워크 오류 발생")
        }
    }
}
