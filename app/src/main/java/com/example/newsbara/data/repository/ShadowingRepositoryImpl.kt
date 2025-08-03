package com.example.newsbara.data.repository

import com.example.newsbara.data.model.shadowing.PronunciationDto
import com.example.newsbara.data.service.ShadowingService
import com.example.newsbara.data.service.TestService
import com.example.newsbara.domain.repository.ShadowingRepository
import com.example.newsbara.domain.repository.TestRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class ShadowingRepositoryImpl @Inject constructor(
    private val shadowingService: ShadowingService
) : ShadowingRepository {
    override suspend fun evaluatePronunciation(
        script: String,
        audioFile: File
    ): Result<PronunciationDto> {
        return try {
            val requestBody = audioFile.asRequestBody("audio/wav".toMediaTypeOrNull())
            val audioPart = MultipartBody.Part.createFormData("audio", audioFile.name, requestBody)

            val response = shadowingService.evaluateShadowing(script, audioPart)

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.isSuccess == true && body.result != null) {
                    Result.success(body.result)
                } else {
                    Result.failure(Throwable("API 실패: ${body?.message ?: "Unknown error"}"))
                }
            } else {
                Result.failure(Throwable("HTTP 실패: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}