package com.example.newsbara.domain.repository

import com.example.newsbara.data.model.recommend.RecommendedVideoDto
import com.example.newsbara.presentation.common.ResultState

interface RecommendRepository {
    suspend fun fetchRecommendedVideos(channelName: String): ResultState<List<RecommendedVideoDto>>
}