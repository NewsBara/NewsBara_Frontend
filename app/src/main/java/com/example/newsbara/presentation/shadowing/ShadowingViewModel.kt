package com.example.newsbara.presentation.shadowing

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsbara.data.model.shadowing.PronunciationDto
import com.example.newsbara.domain.model.ScriptLine
import com.example.newsbara.domain.repository.ShadowingRepository
import com.example.newsbara.domain.repository.VideoRepository
import com.example.newsbara.presentation.common.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ShadowingViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
    private val shadowingRepository: ShadowingRepository
) : ViewModel() {

    private val _scriptLines = MutableStateFlow<ResultState<List<ScriptLine>>>(ResultState.Loading)
    val scriptLines: StateFlow<ResultState<List<ScriptLine>>> = _scriptLines

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex

    private val _pronunciationResult = MutableStateFlow<ResultState<PronunciationDto>>(ResultState.Idle)
    val pronunciationResult: StateFlow<ResultState<PronunciationDto>> = _pronunciationResult

    fun fetchScriptLines(videoId: String) {
        viewModelScope.launch {
            _scriptLines.value = videoRepository.fetchScript(videoId)
            _currentIndex.value = 0 // 항상 처음부터 시작
        }
    }

    fun getCurrentLine(): ScriptLine? {
        val scripts = (_scriptLines.value as? ResultState.Success)?.data ?: return null
        return scripts.getOrNull(_currentIndex.value)
    }

    fun hasNextLine(): Boolean {
        val scripts = (_scriptLines.value as? ResultState.Success)?.data ?: return false
        return _currentIndex.value + 1 < scripts.size
    }

    fun setScriptLines(lines: List<ScriptLine>) {
        _scriptLines.value = ResultState.Success(lines)
        _currentIndex.value = 0
    }

    fun nextLine() {
        _currentIndex.value += 1
    }

//    fun resetIndex() {
//        _currentIndex.value = 0
//    }

    fun evaluatePronunciation(script: String, audioFile: File) {
        viewModelScope.launch {
            _pronunciationResult.value = ResultState.Loading
            try {
                val result = shadowingRepository.evaluatePronunciation(script, audioFile)
                Log.d("evaluate", "✅ evaluatePronunciation 완료됨: $result")
                _pronunciationResult.value = ResultState.Success(result.getOrThrow())
            } catch (e: Exception) {
                Log.e("ShadowingViewModel", "발음 평가 실패 detail: ${e.message}", e)
                _pronunciationResult.value = ResultState.Failure("발음 평가 실패: ${e.message}")
            }
        }
    }

    fun resetPronunciationResult() {
        _pronunciationResult.value = ResultState.Idle
    }
}