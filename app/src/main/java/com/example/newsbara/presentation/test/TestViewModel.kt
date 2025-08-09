package com.example.newsbara.presentation.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsbara.data.model.test.TestGenerationResponse
import com.example.newsbara.domain.repository.TestRepository
import com.example.newsbara.presentation.util.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor(
    private val testRepository: TestRepository
) : ViewModel() {

    private val _testData = MutableStateFlow<ResultState<TestGenerationResponse>>(ResultState.Idle)
    val testData: StateFlow<ResultState<TestGenerationResponse>> = _testData

    fun loadTest(videoId: String) {
        _testData.value = ResultState.Loading
        viewModelScope.launch {
            val result = testRepository.generateTest(videoId)
            _testData.value = result
        }
    }
}