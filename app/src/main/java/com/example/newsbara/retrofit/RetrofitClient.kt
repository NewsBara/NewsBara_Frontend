package com.example.newsbara.retrofit

import com.example.newsbara.data.service.YouTubeApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {

    private const val BASE_URL = "https://your.api.base.url"
    private const val YOUTUBE_URL = "https://www.googleapis.com/youtube/v3/"

    val authRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val youtubeRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(YOUTUBE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
