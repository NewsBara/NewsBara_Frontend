package com.example.newsbara

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsbara.data.model.mypage.BadgeInfo
import com.example.newsbara.data.model.Friend
import com.example.newsbara.data.model.history.HistoryItem
import com.example.newsbara.data.model.mypage.MyPageInfo
import com.example.newsbara.data.model.youtube.SubtitleLine
import com.example.newsbara.domain.repository.MyPageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SharedViewModel @Inject constructor(
    private val myPageRepository: MyPageRepository
) : ViewModel() {
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

    // 친구 목록 (ranking용)
    private val _friends = MutableLiveData<List<Friend>>(emptyList())
    val friends: LiveData<List<Friend>> = _friends

    // 친구 요청 목록 (request용)
    private val _friendRequests = MutableLiveData<List<Friend>>(emptyList())
    val friendRequests: LiveData<List<Friend>> = _friendRequests

    // 검색 결과 목록 (add용)
    private val _searchResults = MutableLiveData<List<Friend>>(emptyList())
    val searchResults: LiveData<List<Friend>> = _searchResults


    fun addFriend(friend: Friend) {
        val current = _friends.value ?: emptyList()
        if (current.any { it.id == friend.id }) return  // 중복 추가 방지
        _friends.value = current + friend
    }

    // 친구 요청 수락 시
    fun acceptFriendRequest(friend: Friend) {
        _friendRequests.value = _friendRequests.value?.filterNot { it.id == friend.id }
        _friends.value = _friends.value?.plus(friend)
    }

    fun rejectFriendRequest(friend: Friend) {
        _friendRequests.value = _friendRequests.value?.filterNot { it.id == friend.id }
    }

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

    fun setHistory(list: List<HistoryItem>) {
        _historyList.value = list
    }

    // 학습 기록 가져오기
    fun fetchHistory() {
        viewModelScope.launch {
            try {
                val result = myPageRepository.getHistory()  // 실제 API 호출
                _historyList.value = result
            } catch (e: Exception) {
                Log.e("SharedViewModel", "학습 기록 불러오기 실패", e)
            }
        }
    }

    fun addToHistory(video: HistoryItem) {
        val currentList = _historyList.value?.toMutableList() ?: mutableListOf()

        // 중복 제거 및 최신순 정렬
        currentList.removeAll { it.videoId == video.videoId }
        currentList.add(0, video)

        _historyList.value = currentList
    }

}

