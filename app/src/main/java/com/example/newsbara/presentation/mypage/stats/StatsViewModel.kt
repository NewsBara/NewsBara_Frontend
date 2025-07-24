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


    fun updateHistoryStatus(item: HistoryItem, onComplete: (HistoryItem?) -> Unit) {
        val nextStatus = when (item.status.uppercase()) {
            "WATCHED" -> "SHADOWING"
            "SHADOWING" -> "TEST"
            "TEST" -> "DICTIONARY"
            "DICTIONARY" -> null
            else -> null
        }

        if (nextStatus == null) {
            onComplete(null)
            return
        }

        val updatedItem = item.copy(status = nextStatus)

        val request = SaveHistoryRequest(
            videoId = updatedItem.videoId,
            title = updatedItem.title,
            thumbnail = updatedItem.thumbnail,
            channel = updatedItem.channel,
            length = updatedItem.length,
            category = updatedItem.category,
            status = updatedItem.status
        )

        saveHistory(request) {
            if (it) onComplete(updatedItem) else onComplete(null)
        }
    }

    fun saveHistory(request: SaveHistoryRequest, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                myPageRepository.saveHistory(request)
                onResult(true)
            } catch (e: Exception) {
                Log.e("StatsViewModel", "히스토리 저장 실패: ${e.message}")
                onResult(false)
            }
        }
    }
}

