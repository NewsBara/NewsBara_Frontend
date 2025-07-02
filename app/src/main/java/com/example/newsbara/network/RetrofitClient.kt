package com.example.newsbara.network

import com.example.newsbara.BuildConfig
import com.example.newsbara.data.service.YouTubeApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {
    private const val YOUTUBE_URL = "https://www.googleapis.com/youtube/v3/"

    val youtubeRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(YOUTUBE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val youtubeService: YouTubeApiService by lazy {
        youtubeRetrofit.create(YouTubeApiService::class.java)
    }
}

