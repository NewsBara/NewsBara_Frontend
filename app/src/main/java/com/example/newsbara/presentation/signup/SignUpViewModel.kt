package com.example.newsbara.presentation.signup

import com.example.newsbara.data.model.signup.SignUpRequest
import com.example.newsbara.domain.repository.AuthRepository
import com.example.newsbara.presentation.util.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow


@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _signUpResult = MutableStateFlow<ResultState<Unit>>(ResultState.Idle)
    val signUpResult: StateFlow<ResultState<Unit>> = _signUpResult

    fun signUp(request: SignUpRequest) {
        viewModelScope.launch {
            _signUpResult.value = ResultState.Loading
            val result = authRepository.signUp(request)
            _signUpResult.value = result.fold(
                onSuccess = { ResultState.Success(Unit) },
                onFailure = { ResultState.Failure(it.message ?: "알 수 없는 오류") }
            )
        }
    }
}

