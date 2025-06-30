package com.example.newsbara.di

import com.example.newsbara.data.api.AuthService
import com.example.newsbara.data.api.MyPageService
import com.example.newsbara.data.api.YouTubeApiService
import com.example.newsbara.data.repository.AuthRepository
import com.example.newsbara.data.repository.AuthRepositoryImpl
import com.example.newsbara.data.repository.MyPageRepository
import com.example.newsbara.data.repository.MyPageRepositoryImpl
import com.example.newsbara.data.service.AuthService
import com.example.newsbara.data.service.MyPageService
import com.example.newsbara.data.service.YouTubeApiService
import com.example.newsbara.domain.repository.AuthRepository
import com.example.newsbara.retrofit.RetrofitClient
import com.example.newsbara.util.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ✅ Retrofit 기반 API 인터페이스 주입
    @Provides
    @Singleton
    fun provideAuthService(): AuthService =
        RetrofitClient.authRetrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    fun provideMyPageService(): MyPageService =
        RetrofitClient.apiRetrofit.create(MyPageService::class.java)

    @Provides
    @Singleton
    fun provideYouTubeService(): YouTubeApiService =
        RetrofitClient.youtubeRetrofit.create(YouTubeApiService::class.java)

    // ✅ Repository 주입
    @Provides
    @Singleton
    fun provideAuthRepository(service: AuthService): AuthRepository =
        AuthRepositoryImpl(service)

    @Provides
    @Singleton
    fun provideMyPageRepository(service: MyPageService): MyPageRepository =
        MyPageRepositoryImpl(service)
}
