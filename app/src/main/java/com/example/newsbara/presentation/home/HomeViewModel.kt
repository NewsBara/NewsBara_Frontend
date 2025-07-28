package com.example.newsbara.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsbara.data.model.youtube.VideoSection
import com.example.newsbara.domain.repository.YouTubeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val youTubeRepository: YouTubeRepository
) : ViewModel() {

    private val _videoSections = MutableStateFlow<List<VideoSection>>(emptyList())
    val videoSections: StateFlow<List<VideoSection>> = _videoSections

    fun fetchVideoSections(channels: Map<String, String>) {

        viewModelScope.launch {
            Log.d("HomeViewModel", "✅ fetchVideoSections() 호출됨")
            try {
                val result = youTubeRepository.fetchVideoSections(channels)
                _videoSections.value = result

            } catch (e: Exception) {
                // 에러 로깅 또는 별도 상태 처리
                Log.e("HomeViewModel", "🚨 fetchVideoSections 예외 발생: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}
