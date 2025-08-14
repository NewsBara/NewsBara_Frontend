package com.example.newsbara.presentation.video

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsbara.domain.model.KeywordInfo
import com.example.newsbara.domain.model.ScriptLine
import com.example.newsbara.domain.model.toScriptLine
import com.example.newsbara.domain.repository.VideoRepository
import com.example.newsbara.presentation.common.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val videoRepository: VideoRepository
) : ViewModel() {

    private val _scriptLines = MutableStateFlow<ResultState<List<ScriptLine>>>(ResultState.Loading)
    val scriptLines: StateFlow<ResultState<List<ScriptLine>>> = _scriptLines

    private val _keywordMap = MutableStateFlow<Map<String, KeywordInfo>>(emptyMap())
    val keywordMap: StateFlow<Map<String, KeywordInfo>> = _keywordMap

    fun fetchScript(videoId: String) {
        viewModelScope.launch {
            _scriptLines.value = try {
                val response = videoRepository.fetchScript(videoId)
                when (response) {
                    is ResultState.Success -> ResultState.Success(response.data)
                    is ResultState.Failure -> ResultState.Failure(response.message)
                    else -> ResultState.Loading
                }
            } catch (e: Exception) {
                ResultState.Failure("자막 불러오기 실패: ${e.message}")
            }
        }
    }

    fun getKoDefinitions(word: String): Pair<String,String>? {
        val k = _keywordMap.value[word.lowercase()] ?: return null
        val first = k.gptDefinitionKo.ifBlank { k.bertDefinitionKo.orEmpty() }
        val second = k.bertDefinitionKo?.ifBlank { k.gptDefinitionKo } ?: k.gptDefinitionKo
        return first to second
    }
}