package com.example.newsbara.presentation.shadowing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsbara.domain.model.ScriptLine
import com.example.newsbara.domain.repository.VideoRepository
import com.example.newsbara.presentation.common.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShadowingViewModel @Inject constructor(
    private val videoRepository: VideoRepository
) : ViewModel() {

    private val _scriptLines = MutableStateFlow<ResultState<List<ScriptLine>>>(ResultState.Loading)
    val scriptLines: StateFlow<ResultState<List<ScriptLine>>> = _scriptLines

    fun fetchScriptLines(videoId: String) {
        viewModelScope.launch {
            _scriptLines.value = videoRepository.fetchScript(videoId)
        }
    }
}