package com.example.newsbara

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsbara.data.model.friends.FriendDecisionRequest
import com.example.newsbara.data.model.friends.FriendListItem
import com.example.newsbara.data.model.friends.FriendRequestItem
import com.example.newsbara.data.model.friends.FriendSearchResponse
import com.example.newsbara.data.model.history.HistoryItem
import com.example.newsbara.data.model.youtube.SubtitleLine
import com.example.newsbara.domain.repository.FriendRepository
import com.example.newsbara.domain.repository.MyPageRepository
import com.example.newsbara.presentation.util.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SharedViewModel @Inject constructor(
    private val myPageRepository: MyPageRepository,
    private val friendRepository: FriendRepository
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
    private val _friends = MutableLiveData<List<FriendListItem>>(emptyList())
    val friends: LiveData<List<FriendListItem>> = _friends

    // 친구 요청 목록 (request용)
    private val _friendRequests = MutableLiveData<List<FriendRequestItem>>(emptyList())
    val friendRequests: LiveData<List<FriendRequestItem>> = _friendRequests

    // 검색 결과 목록 (add용)
    private val _searchResults = MutableLiveData<List<FriendSearchResponse>>(emptyList())
    val searchResults: LiveData<List<FriendSearchResponse>> = _searchResults

    fun fetchFriends() {
        viewModelScope.launch {
            when (val result = friendRepository.getFriends()) {
                is ResultState.Success -> {
                    _friends.value = result.data
                }
                is ResultState.Failure -> {
                    Log.e("fetchFriends", "❌ 친구 목록 실패(Failure): ${result.message}")
                }
                is ResultState.Error -> {
                    Log.e("fetchFriends", "❌ 친구 목록 실패(Error): ", result.exception)
                }
                is ResultState.Loading -> {
                    // 로딩 중 처리 (필요 시 로딩 인디케이터 보여줄 수도 있음)
                }
                is ResultState.Idle -> {
                    // 아직 아무 작업도 시작되지 않은 상태 (특별한 처리 없으면 비워도 됨)
                }
            }
        }
    }

    fun fetchFriendRequests() {
        viewModelScope.launch {
            when (val result = friendRepository.getFriendRequests()) {
                is ResultState.Success -> {
                    _friendRequests.value = result.data
                }
                is ResultState.Failure -> {
                    Log.e("FriendRequest", "❌ 요청 목록 실패(Failure): ${result.message}")
                }
                is ResultState.Error -> {
                    Log.e("FriendRequest", "❌ 요청 목록 실패(Error)", result.exception)
                }
                is ResultState.Loading -> {
                    // 로딩 중 처리 시 여기에
                    Log.d("FriendRequest", "⏳ 친구 요청 목록 불러오는 중...")
                }
                is ResultState.Idle -> {
                    // 초기 상태일 경우 특별히 할 일 없다면 생략 가능
                    Log.d("FriendRequest", "⏸️ Idle 상태")
                }
            }
        }
    }

    // 친구 요청 수락
    fun acceptFriendRequest(friend: FriendRequestItem) {
        viewModelScope.launch {
            when (val result = friendRepository.decideFollowRequest(
                friend.id, FriendDecisionRequest("ACCEPTED"))
            ) {
                is ResultState.Success -> {
                    // ✅ 수락된 요청은 목록에서 제거
                    _friendRequests.value = _friendRequests.value?.filterNot { it.id == friend.id }

                    // ✅ 친구 목록 새로고침
                    fetchFriends()
                }
                is ResultState.Failure -> {
                    Log.e("FriendRequest", "요청 수락 실패(Failure): ${result.message}")
                }
                is ResultState.Error -> {
                    Log.e("FriendRequest", "요청 수락 실패(Error)", result.exception)
                }
                else -> Unit
            }
        }
    }


    fun rejectFriendRequest(friend: FriendRequestItem) {
        viewModelScope.launch {
            when (val result = friendRepository.decideFollowRequest(
                friend.id, FriendDecisionRequest("REJECTED"))
            ) {
                is ResultState.Success -> {
                    _friendRequests.value = _friendRequests.value?.filterNot { it.id == friend.id }
                }
                is ResultState.Failure -> {
                    Log.e("FriendRequest", "요청 거절 실패(Failure): ${result.message}")
                }
                is ResultState.Error -> {
                    Log.e("FriendRequest", "요청 거절 실패(Error)", result.exception)
                }
                is ResultState.Loading -> {
                    // 필요시 로딩 처리
                }
                is ResultState.Idle -> {
                    // 초기 상태
                }
            }
        }
    }


    fun sendFriendRequest(userId: Int) {
        viewModelScope.launch {
            when (val result = friendRepository.sendFriendRequest(userId)) {
                is ResultState.Success -> {
                    Log.d("SendFriend", "요청 성공: ${result.data}")
                }
                is ResultState.Failure -> {
                    Log.e("SendFriend", "요청 실패(Failure): ${result.message}")
                }
                is ResultState.Error -> {
                    Log.e("SendFriend", "요청 실패(Error)", result.exception)
                }
                is ResultState.Loading, is ResultState.Idle -> {

                }
            }
        }
    }

    fun searchUsers(name: String) {
        viewModelScope.launch {
            when (val result = friendRepository.searchUsers(name)) {
                is ResultState.Success -> {
                    _searchResults.value = result.data
                }
                is ResultState.Failure -> {
                    Log.e("Search", "검색 실패(Failure): ${result.message}")
                }
                is ResultState.Error -> {
                    Log.e("Search", "검색 실패(Error)", result.exception)
                }
                is ResultState.Loading -> {
                    // 필요시 로딩 처리
                }
                is ResultState.Idle -> {
                    // 초기 상태일 경우 처리
                }
            }
        }
    }

    fun setVideoProgress(item: HistoryItem) {
        _videoId.value = item.videoId
        _videoTitle.value = item.title
        _thumbnailUrl.value = item.thumbnail
    }

    fun setVideoData(id: String, title: String, subs: List<SubtitleLine>) {
        Log.d("SharedViewModel", "✅ setVideoData 호출됨: $id")
        _videoId.value = id
        _videoTitle.value = title
        _subtitleList.value = subs
    }

    fun setHistory(list: List<HistoryItem>) {
        _historyList.value = list
    }

    fun fetchHistory() {
        viewModelScope.launch {
            when (val result = myPageRepository.getHistory()) {
                is ResultState.Success -> {
                    _historyList.value = result.data
                }
                is ResultState.Failure -> {
                    Log.e("SharedViewModel", "❌ 학습 기록 불러오기 실패 (Failure): ${result.message}")
                }
                is ResultState.Error -> {
                    Log.e("SharedViewModel", "❌ 학습 기록 불러오기 실패 (Error)", result.exception)
                }
                is ResultState.Loading -> {
                    Log.d("SharedViewModel", "⏳ 학습 기록 불러오는 중...")
                }
                is ResultState.Idle -> {
                    Log.d("SharedViewModel", "ℹ️ 학습 기록 Idle 상태")
                }
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

