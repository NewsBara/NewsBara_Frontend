package com.example.newsbara.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsbara.data.model.login.LoginRequest
import com.example.newsbara.data.model.login.LoginResponse
import com.example.newsbara.domain.repository.AuthRepository
import com.example.newsbara.presentation.common.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginResult = MutableStateFlow<ResultState<LoginResponse>>(ResultState.Idle)
    val loginResult: StateFlow<ResultState<LoginResponse>> = _loginResult

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _loginResult.value = ResultState.Loading
            val result = authRepository.login(request)
            _loginResult.value = result.fold(
                onSuccess = { ResultState.Success(it) },
                onFailure = { ResultState.Failure(it.message ?: "로그인 실패") }
            )
        }
    }
}
