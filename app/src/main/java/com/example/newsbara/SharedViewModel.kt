package com.example.newsbara

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newsbara.data.ShadowingSentence
import com.example.newsbara.data.SubtitleLine

class SharedViewModel : ViewModel() {
    val highlightWords = listOf("accelerating", "global", "urgent")

    private val _videoId = MutableLiveData<String>()
    val videoId: LiveData<String> get() = _videoId

    private val _videoTitle = MutableLiveData<String>()
    val videoTitle: LiveData<String> get() = _videoTitle

    private val _subtitleList = MutableLiveData<List<SubtitleLine>>()
    val subtitleList: MutableLiveData<List<SubtitleLine>> get() = _subtitleList

    fun setVideoData(id: String, title: String, subs: List<SubtitleLine>) {
        _videoId.value = id
        _videoTitle.value = title
        _subtitleList.value = subs
    }
}

