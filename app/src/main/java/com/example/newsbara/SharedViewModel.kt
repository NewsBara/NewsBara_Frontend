package com.example.newsbara

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newsbara.data.ShadowingSentence
import com.example.newsbara.data.SubtitleLine
import com.example.newsbara.data.VideoItem
import com.example.newsbara.data.VideoProgress

class SharedViewModel : ViewModel() {
    val highlightWords = listOf("accelerating", "global", "urgent")

    private val _videoId = MutableLiveData<String>()
    val videoId: LiveData<String> get() = _videoId

    private val _videoTitle = MutableLiveData<String>()
    val videoTitle: LiveData<String> get() = _videoTitle

    private val _subtitleList = MutableLiveData<List<SubtitleLine>>()
    val subtitleList: MutableLiveData<List<SubtitleLine>> get() = _subtitleList

    private val _historyList = MutableLiveData<List<VideoItem>>(mutableListOf())
    val historyList: LiveData<List<VideoItem>> get() = _historyList

    private val _videoProgress = MutableLiveData<VideoProgress>()
    val videoProgress: LiveData<VideoProgress> get() = _videoProgress

    private val _friendList = MutableLiveData<List<Friend>>()
    val friendList: LiveData<List<Friend>> get() = _friendList


    fun setVideoProgress(progress: VideoProgress) {
        _videoProgress.value = progress
    }


    fun setVideoData(id: String, title: String, subs: List<SubtitleLine>) {
        _videoId.value = id
        _videoTitle.value = title
        _subtitleList.value = subs
    }

    fun addToHistory(video: VideoItem) {
        val currentList = _historyList.value?.toMutableList() ?: mutableListOf()

        // 중복 제거 및 최신순 정렬
        currentList.removeAll { it.videoId == video.videoId }
        currentList.add(0, video)

        _historyList.value = currentList
    }

}

