package com.example.newsbara.network

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

// TokenInterceptor.kt
class TokenInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
            .getString("accessToken", null)

        Log.d("Interceptor", "🪪 AccessToken = $token")

        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        // 토큰이 있을 경우 Authorization 헤더 추가
        token?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        val newRequest = requestBuilder.build()
        return chain.proceed(newRequest)
    }
}
