package com.example.newsbara.domain.repository

import com.example.newsbara.data.model.test.TestGenerationResponse
import com.example.newsbara.data.service.TestService
import com.example.newsbara.presentation.util.ResultState
import javax.inject.Inject

interface TestRepository {
    suspend fun generateTest(videoId: String): ResultState<TestGenerationResponse>
}
