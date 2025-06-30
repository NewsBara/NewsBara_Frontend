package com.example.newsbara.presentation.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newsbara.data.model.mypage.BadgeInfo
import com.example.newsbara.data.model.mypage.MyPageInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
) : ViewModel()  {

    private val _myPageInfo = MutableLiveData<MyPageInfo>()
    val myPageInfo: LiveData<MyPageInfo> = _myPageInfo

    private val _badgeInfo = MutableLiveData<BadgeInfo>()
    val badgeInfo: LiveData<BadgeInfo> get() = _badgeInfo

    fun setMyPageInfo(info: MyPageInfo) {
        _myPageInfo.value = info
    }

    fun setBadgeInfo(info: BadgeInfo) {
        _badgeInfo.value = info
    }

}