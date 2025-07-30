package com.example.newsbara.data.repository

import com.example.newsbara.data.model.recommend.RecommendedVideoDto
import com.example.newsbara.data.service.RecommendService
import com.example.newsbara.domain.repository.RecommendRepository
import com.example.newsbara.presentation.common.ResultState
import javax.inject.Inject

class RecommendRepositoryImpl @Inject constructor(
    private val recommendService: RecommendService
) : RecommendRepository {

    override suspend fun fetchRecommendedVideos(): ResultState<List<RecommendedVideoDto>> {
        return try {
            val response = recommendService.getRecommendedVideos()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.isSuccess) {
                    ResultState.Success(body.result.recommendList)
                } else {
                    ResultState.Failure(body?.message ?: "응답 오류")
                }
            } else {
                ResultState.Failure("HTTP 오류: ${response.code()}")
            }
        } catch (e: Exception) {
            ResultState.Failure("예외 발생: ${e.localizedMessage ?: "알 수 없음"}")
        }
    }
}