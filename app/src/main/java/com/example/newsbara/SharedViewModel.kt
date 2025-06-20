package com.example.newsbara

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newsbara.data.BadgeInfo
import com.example.newsbara.data.Friend
import com.example.newsbara.data.HistoryItem
import com.example.newsbara.data.MyPageInfo
import com.example.newsbara.data.SubtitleLine


class SharedViewModel : ViewModel() {
    val highlightWords = listOf("accelerating", "global", "urgent")

    private val _videoId = MutableLiveData<String>()
    val videoId: LiveData<String> get() = _videoId

    private val _videoTitle = MutableLiveData<String>()
    val videoTitle: LiveData<String> get() = _videoTitle

    private val _thumbnailUrl = MutableLiveData<String>()
    val thumbnailUrl: LiveData<String> get() = _thumbnailUrl

    private val _subtitleList = MutableLiveData<List<SubtitleLine>>()
    val subtitleList: MutableLiveData<List<SubtitleLine>> get() = _subtitleList

    private val _historyList = MutableLiveData<List<HistoryItem>>(mutableListOf())
    val historyList: LiveData<List<HistoryItem>> get() = _historyList

    private val _myPageInfo = MutableLiveData<MyPageInfo>()
    val myPageInfo: LiveData<MyPageInfo> = _myPageInfo

    private val _badgeInfo = MutableLiveData<BadgeInfo>()
    val badgeInfo: LiveData<BadgeInfo> get() = _badgeInfo

    private val _friendList = MutableLiveData<List<Friend>>()
    val friendList: LiveData<List<Friend>> get() = _friendList

    fun setVideoProgress(item: HistoryItem) {
        _videoId.value = item.videoId
        _videoTitle.value = item.title
        _thumbnailUrl.value = item.thumbnail
    }

    fun setVideoData(id: String, title: String, subs: List<SubtitleLine>) {
        _videoId.value = id
        _videoTitle.value = title
        _subtitleList.value = subs
    }

    fun setMyPageInfo(info: MyPageInfo) {
        _myPageInfo.value = info
    }

    fun setBadgeInfo(info: BadgeInfo) {
        _badgeInfo.value = info
    }

    fun addToHistory(video: HistoryItem) {
        val currentList = _historyList.value?.toMutableList() ?: mutableListOf()

        // 중복 제거 및 최신순 정렬
        currentList.removeAll { it.videoId == video.videoId }
        currentList.add(0, video)

        _historyList.value = currentList
    }

}

