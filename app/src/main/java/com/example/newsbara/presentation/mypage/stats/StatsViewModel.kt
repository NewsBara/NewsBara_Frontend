package com.example.newsbara.presentation.mypage.stats

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsbara.data.model.history.HistoryItem
import com.example.newsbara.data.model.history.SaveHistoryRequest
import com.example.newsbara.domain.repository.MyPageRepository
import com.example.newsbara.presentation.util.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class StatsViewModel @Inject constructor(
    private val myPageRepository: MyPageRepository
) : ViewModel() {

    private val _historyList = MutableStateFlow<List<HistoryItem>>(emptyList())
    val historyList: StateFlow<List<HistoryItem>> = _historyList

    fun fetchHistory() {
        viewModelScope.launch {
            when (val result = myPageRepository.getHistory()) {
                is ResultState.Success -> {
                    _historyList.value = result.data
                }
                is ResultState.Failure -> {
                    Log.e("StatsViewModel", "히스토리 로딩 실패: ${result.message}")
                }
                is ResultState.Error -> {
                    Log.e("StatsViewModel", "히스토리 오류: ${result.exception}")
                }
                else -> Unit
            }
        }
    }


    fun updateHistoryStatus(item: HistoryItem, onComplete: (HistoryItem?) -> Unit = {}) {
        val next = when (item.status.uppercase()) {
            "WATCHED" -> "SHADOWING"
            "SHADOWING" -> "TEST"
            "TEST" -> "WORD"
            "WORD"      -> "COMPLETED"
            else -> null
        } ?: run { onComplete(null); return }

        val req = SaveHistoryRequest(
            videoId = item.videoId,
            title = item.title,
            thumbnail = item.thumbnail,
            channel = item.channel,
            length = item.length,
            category = item.category,
            status = next
        )
        Log.d("StatsViewModel", "saveHistory 호출: videoId=${req.videoId}, status=${req.status}")

        viewModelScope.launch {
            when (val r = myPageRepository.saveHistory(req)) {
                is ResultState.Success -> {
                    val updated = r.data
                    _historyList.value = _historyList.value.map {
                        if (it.videoId == updated.videoId) updated else it
                    }
                    onComplete(updated)
                }
                is ResultState.Failure -> { Log.e("StatsVM","저장 실패: ${r.message}"); onComplete(null) }
                is ResultState.Error   -> { Log.e("StatsVM","저장 에러: ${r.exception}"); onComplete(null) }
                else -> onComplete(null)
            }
        }
    }


    fun saveWatchedHistory(video: HistoryItem, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val req = SaveHistoryRequest(
                videoId = video.videoId,
                title = video.title,
                thumbnail = video.thumbnail,
                channel = video.channel,
                length = video.length,
                category = video.category,
                status = "WATCHED"
            )
            when (myPageRepository.saveHistory(req)) {
                is ResultState.Success -> onResult(true)
                else -> onResult(false)
            }
        }
    }

}

