package com.example.newsbara.domain.repository

import com.example.newsbara.data.model.shadowing.PronunciationDto
import java.io.File

interface ShadowingRepository {
    suspend fun evaluatePronunciation(script: String, audioFile: File): Result<PronunciationDto>
}