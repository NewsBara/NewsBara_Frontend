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

    // MyPageViewModel.kt
    private val _myPageInfo = MutableStateFlow<MyPageInfo?>(null)
    val myPageInfo: StateFlow<MyPageInfo?> = _myPageInfo

    private val _badgeInfo = MutableLiveData<BadgeInfo>()
    val badgeInfo: LiveData<BadgeInfo> get() = _badgeInfo

    private val _uploadResult = MutableStateFlow<ResultState<UpdateProfileImageResponse>>(ResultState.Idle)
    val uploadResult: StateFlow<ResultState<UpdateProfileImageResponse>> = _uploadResult

    private val _logoutResult = MutableStateFlow<ResultState<Unit>>(ResultState.Idle)
    val logoutResult: StateFlow<ResultState<Unit>> = _logoutResult

    private val _nameUpdateResult = MutableStateFlow<ResultState<UpdateNameResponse>>(ResultState.Idle)
    val nameUpdateResult: StateFlow<ResultState<UpdateNameResponse>> = _nameUpdateResult

    fun updateName(newName: String) {
        viewModelScope.launch {
            _nameUpdateResult.value = ResultState.Loading
            try {
                val result = repository.updateName(UpdateNameRequest(newName))
                Log.d("MyPageViewModel", "현재 myPageInfo 상태: ${_myPageInfo.value}")

                // UI 반영용 데이터도 업데이트
                _myPageInfo.value = _myPageInfo.value?.copy(name = result.name)
                _nameUpdateResult.value = ResultState.Success(result)
                fetchMyPageInfo()
            } catch (e: Exception) {
                _nameUpdateResult.value = ResultState.Failure(e.message ?: "이름 변경 실패")
            }
        }
    }


    fun fetchMyPageInfo() {
        viewModelScope.launch {
            try {
                val info = repository.getMyPageInfo()
                _myPageInfo.value = info
            } catch (e: Exception) {
                Log.e("MyPageViewModel", "마이페이지 정보 가져오기 실패: ${e.message}")
            }
        }
    }


    fun fetchBadgeInfo() {
        viewModelScope.launch {
            try {
                val badge = repository.getBadge()
                _badgeInfo.value = badge
            } catch (e: Exception) {
                Log.e("MyPageViewModel", "배지 정보 가져오기 실패: ${e.message}")
            }
        }
    }


    fun uploadProfileImage(file: File) {
        viewModelScope.launch {
            _uploadResult.value = ResultState.Loading
            try {
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                val result = repository.updateProfileImage(body)
                _uploadResult.value = ResultState.Success(result)
            } catch (e: Exception) {
                _uploadResult.value = ResultState.Failure(e.message ?: "에러 발생")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _logoutResult.value = ResultState.Loading
            try {
                authRepository.logout() // 실제 API 호출
                // 🔐 accessToken 삭제
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
}