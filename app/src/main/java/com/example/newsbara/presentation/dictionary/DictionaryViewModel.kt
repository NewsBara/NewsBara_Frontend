package com.example.newsbara.presentation.dictionary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsbara.domain.model.DictionaryEntry
import com.example.newsbara.domain.repository.VideoRepository
import com.example.newsbara.presentation.common.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DictionaryViewModel @Inject constructor(
    private val videoRepository: VideoRepository
) : ViewModel() {

    private val _entries =
        MutableStateFlow<ResultState<List<DictionaryEntry>>>(ResultState.Loading)
    val entries: StateFlow<ResultState<List<DictionaryEntry>>> = _entries

    fun load(videoId: String) {
        viewModelScope.launch {
            _entries.value = ResultState.Loading
            _entries.value = videoRepository.fetchDictionaryEntries(videoId)
        }
    }
}