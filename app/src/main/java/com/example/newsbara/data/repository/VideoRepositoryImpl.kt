package com.example.newsbara.data.repository

import com.example.newsbara.data.model.script.ScriptResponseDto
import com.example.newsbara.data.service.VideoService
import com.example.newsbara.domain.repository.VideoRepository
import com.example.newsbara.presentation.common.ResultState
import javax.inject.Inject

class VideoRepositoryImpl @Inject constructor(
    private val videoService: VideoService
) : VideoRepository {

    override suspend fun fetchScript(videoId: String): ResultState<List<ScriptResponseDto>> {
            return try {
                val response = videoService.getScriptByVideoId(videoId)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.isSuccess) {
                        ResultState.Success(body.result)
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
