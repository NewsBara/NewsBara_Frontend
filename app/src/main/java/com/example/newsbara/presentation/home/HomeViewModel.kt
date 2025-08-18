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
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
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

            val channelNames = listOf("BBC News", "CNN")
            for (channel in channelNames) {
                val currentChannel = channel
                val result = recommendRepository.fetchRecommendedVideos(currentChannel)
                Log.d("HomeViewModel", "🔵 $channel 추천 API 응답: $result")

                if (result is ResultState.Success) {
                    Log.d("HomeViewModel", "🎯 $channel 추천 영상 수: ${result.data.size}")

                    val section = VideoSection(
                        channelName = channel,
                        videos = result.data.map {
                            HistoryItem(
                                id = 0,
                                videoId = it.videoId,
                                title = it.title,
                                thumbnail = it.thumbnail,
                                channel = channel,
                                length = it.length,
                                category = it.category,
                                status = "UNWATCHED",
                                createdAt = ""
                            )
                        }
                    )
                    allSections.add(section)
                } else if (result is ResultState.Failure) {
                    Log.e("HomeViewModel", "❌ $channel 실패: ${result}")
                }
            }

            _videoSections.value = allSections
        }
    }
}
