package com.example.newsbara.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsbara.data.model.history.HistoryItem
import com.example.newsbara.data.model.recommend.RecommendedVideoDto
import com.example.newsbara.data.model.youtube.VideoSection
import com.example.newsbara.domain.repository.RecommendRepository
import com.example.newsbara.domain.repository.YouTubeRepository
import com.example.newsbara.presentation.common.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val recommendRepository: RecommendRepository
) : ViewModel() {

    private val _videoSections = MutableStateFlow<List<VideoSection>>(emptyList())
    val videoSections: StateFlow<List<VideoSection>> = _videoSections

    fun fetchAllSections() {
        viewModelScope.launch {
            val allSections = mutableListOf<VideoSection>()
            val recommendResult = recommendRepository.fetchRecommendedVideos()
            Log.d("HomeViewModel", "🔵 추천 API 응답: $recommendResult")

            if (recommendResult is ResultState.Success) {
                Log.d("HomeViewModel", "🎯 추천 영상 수: ${recommendResult.data.size}")

                val grouped = recommendResult.data
                    .filter { it.channel.contains("BBC") || it.channel.contains("CNN") }
                    .groupBy {
                        when {
                            it.channel.contains("BBC") -> "BBC"
                            it.channel.contains("CNN") -> "CNN"
                            else -> "기타"
                        }
                    }

                grouped.forEach { (channelName, items) ->
                    val section = VideoSection(
                        channelName = channelName,
                        videos = items.map {
                            HistoryItem(
                                id = 0,
                                videoId = it.videoId,
                                title = it.title,
                                thumbnail = it.thumbnail,
                                channel = it.channel,
                                length = it.length,
                                category = it.category,
                                status = "UNWATCHED",
                                createdAt = ""
                            )
                        }
                    )
                    allSections.add(section)
                }
            }

            _videoSections.value = allSections
        }
    }
}
