package com.example.newsbara.presentation.mypage

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newsbara.data.model.mypage.BadgeInfo
import com.example.newsbara.data.model.mypage.MyPageInfo
import com.example.newsbara.data.model.mypage.UpdateProfileImageResponse
import com.example.newsbara.domain.repository.MyPageRepository
import com.example.newsbara.presentation.util.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.MultipartBody
import androidx.lifecycle.viewModelScope
import com.example.newsbara.data.model.mypage.UpdateNameRequest
import com.example.newsbara.data.model.mypage.UpdateNameResponse
import com.example.newsbara.domain.repository.AuthRepository
import kotlinx.coroutines.launch
import java.io.File

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val repository: MyPageRepository,
    private val authRepository: AuthRepository,
    private val app: Application
) : ViewModel()  {

    private val _myPageInfo = MutableStateFlow<MyPageInfo?>(null)
    val myPageInfo: StateFlow<MyPageInfo?> = _myPageInfo

    private val _myPageInfoResult = MutableStateFlow<ResultState<MyPageInfo>>(ResultState.Idle)
    val myPageInfoResult: StateFlow<ResultState<MyPageInfo>> = _myPageInfoResult

    private val _badgeInfo = MutableStateFlow<BadgeInfo?>(null)
    val badgeInfo: StateFlow<BadgeInfo?> = _badgeInfo

    private val _badgeInfoResult = MutableStateFlow<ResultState<BadgeInfo>>(ResultState.Idle)
    val badgeInfoResult: StateFlow<ResultState<BadgeInfo>> = _badgeInfoResult

    private val _uploadResult = MutableStateFlow<ResultState<UpdateProfileImageResponse>>(ResultState.Idle)
    val uploadResult: StateFlow<ResultState<UpdateProfileImageResponse>> = _uploadResult

    private val _logoutResult = MutableStateFlow<ResultState<Unit>>(ResultState.Idle)
    val logoutResult: StateFlow<ResultState<Unit>> = _logoutResult

    private val _deleteUserResult = MutableStateFlow<ResultState<Unit>>(ResultState.Idle)
    val deleteUserResult: StateFlow<ResultState<Unit>> = _deleteUserResult

    private val _nameUpdateResult = MutableStateFlow<ResultState<UpdateNameResponse>>(ResultState.Idle)
    val nameUpdateResult: StateFlow<ResultState<UpdateNameResponse>> = _nameUpdateResult

    fun updateName(newName: String) {
        viewModelScope.launch {
            _nameUpdateResult.value = ResultState.Loading
            when (val result = repository.updateName(UpdateNameRequest(newName))) {
                is ResultState.Success -> {
                    val updatedName = result.data.name
                    _myPageInfo.value = _myPageInfo.value?.copy(name = updatedName)
                    _nameUpdateResult.value = result
                    fetchMyPageInfo()
                }

                is ResultState.Failure -> {
                    _nameUpdateResult.value = result
                }

                is ResultState.Error -> {
                    _nameUpdateResult.value = result
                }

                else -> Unit
            }
        }
    }

    fun fetchMyPageInfo() {
        viewModelScope.launch {
            _myPageInfoResult.value = ResultState.Loading
            when (val result = repository.getMyPageInfo()) {
                is ResultState.Success -> {
                    _myPageInfo.value = result.data
                    _myPageInfoResult.value = ResultState.Success(result.data)
                }
                is ResultState.Failure -> {
                    Log.e("MyPageViewModel", "실패: ${result.message}")
                    _myPageInfoResult.value = ResultState.Failure(result.message)
                }
                is ResultState.Error -> {
                    Log.e("MyPageViewModel", "에러: ${result.exception.message}")
                    _myPageInfoResult.value = ResultState.Error(result.exception)
                }
                else -> Unit
            }
        }
    }

    fun fetchBadgeInfo() {
        viewModelScope.launch {
            _badgeInfoResult.value = ResultState.Loading
            when (val result = repository.getBadge()) {
                is ResultState.Success -> _badgeInfoResult.value = ResultState.Success(result.data)
                is ResultState.Failure -> _badgeInfoResult.value = ResultState.Failure(result.message)
                is ResultState.Error -> _badgeInfoResult.value = ResultState.Error(result.exception)
                else -> Unit
            }
        }
    }

    fun uploadProfileImage(file: File) {
        viewModelScope.launch {
            _uploadResult.value = ResultState.Loading
            when (val result = repository.updateProfileImage(
                MultipartBody.Part.createFormData(
                    "file",
                    file.name,
                    file.asRequestBody("image/*".toMediaTypeOrNull())
                )
            )) {
                is ResultState.Success -> _uploadResult.value = ResultState.Success(result.data)
                is ResultState.Failure -> _uploadResult.value = ResultState.Failure(result.message)
                is ResultState.Error -> _uploadResult.value =
                    ResultState.Error(result.exception)
                else -> Unit
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _logoutResult.value = ResultState.Loading
            try {
                authRepository.logout()

                app.getSharedPreferences("auth", Context.MODE_PRIVATE)
                    .edit()
                    .remove("accessToken")
                    .apply()

                _logoutResult.value = ResultState.Success(Unit)
            } catch (e: Exception) {
                _logoutResult.value = ResultState.Failure(e.message ?: "로그아웃 실패")
            }
        }
    }

    fun deleteUser() {
        viewModelScope.launch {
            _deleteUserResult.value = ResultState.Loading
            try {
                authRepository.deleteUser()

                // 토큰 삭제
                app.getSharedPreferences("auth", Context.MODE_PRIVATE)
                    .edit()
                    .remove("accessToken")
                    .apply()

                _deleteUserResult.value = ResultState.Success(Unit)
            } catch (e: Exception) {
                _deleteUserResult.value = ResultState.Error(e)
            }
        }
    }
}