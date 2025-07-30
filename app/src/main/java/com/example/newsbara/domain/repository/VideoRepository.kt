package com.example.newsbara.domain.repository

import com.example.newsbara.data.model.script.ScriptResponseDto
import com.example.newsbara.presentation.common.ResultState

interface VideoRepository {
    suspend fun fetchScript(videoId: String): ResultState<List<ScriptResponseDto>>
}