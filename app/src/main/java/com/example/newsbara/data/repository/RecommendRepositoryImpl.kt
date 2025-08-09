package com.example.newsbara.data.repository

import android.util.Log
import com.example.newsbara.data.model.recommend.RecommendedVideoDto
import com.example.newsbara.data.service.RecommendService
import com.example.newsbara.domain.repository.RecommendRepository
import com.example.newsbara.presentation.common.ResultState
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

class RecommendRepositoryImpl @Inject constructor(
    private val recommendService: RecommendService
) : RecommendRepository {

    override suspend fun fetchRecommendedVideos(channelName: String): ResultState<List<RecommendedVideoDto>> {
        return try {
            val encodedName = URLEncoder.encode(channelName, StandardCharsets.UTF_8.toString()).replace("+", "%20")
            Log.d("HomeViewModel", "📦 인코딩된 채널명: $encodedName")
            val response = recommendService.getRecommendedVideos(encodedName)
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