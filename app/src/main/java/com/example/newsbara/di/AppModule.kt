package com.example.newsbara.di

import android.content.Context
import com.example.newsbara.BuildConfig
import com.example.newsbara.data.repository.AuthRepositoryImpl
import com.example.newsbara.data.repository.FriendRepositoryImpl
import com.example.newsbara.data.repository.MyPageRepositoryImpl
import com.example.newsbara.data.repository.RecommendRepositoryImpl
import com.example.newsbara.data.repository.ShadowingRepositoryImpl
import com.example.newsbara.data.repository.TestRepositoryImpl
import com.example.newsbara.data.repository.VideoRepositoryImpl
import com.example.newsbara.data.service.AuthService
import com.example.newsbara.data.service.FriendService
import com.example.newsbara.data.service.MyPageService
import com.example.newsbara.data.service.RecommendService
import com.example.newsbara.data.service.ShadowingService
import com.example.newsbara.data.service.TestService
import com.example.newsbara.data.service.VideoService
import com.example.newsbara.data.service.YouTubeApiService
import com.example.newsbara.domain.repository.AuthRepository
import com.example.newsbara.domain.repository.FriendRepository
import com.example.newsbara.domain.repository.MyPageRepository
import com.example.newsbara.domain.repository.RecommendRepository
import com.example.newsbara.domain.repository.ShadowingRepository
import com.example.newsbara.domain.repository.TestRepository
import com.example.newsbara.domain.repository.VideoRepository
import com.example.newsbara.network.RetrofitClient
import com.example.newsbara.network.TokenInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTokenInterceptor(@ApplicationContext context: Context): TokenInterceptor =
        TokenInterceptor(context)

    @Provides
    @Singleton
    fun provideOkHttpClient(tokenInterceptor: TokenInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(tokenInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
            .writeTimeout(300, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun provideApiRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    fun provideMyPageService(retrofit: Retrofit): MyPageService =
        retrofit.create(MyPageService::class.java)

    @Provides
    @Singleton
    fun provideFriendService(retrofit: Retrofit): FriendService =
        retrofit.create(FriendService::class.java)

    @Provides
    @Singleton
    fun provideShadowingService(retrofit: Retrofit): ShadowingService =
        retrofit.create(ShadowingService::class.java)

    @Provides
    @Singleton
    fun provideYouTubeService(): YouTubeApiService =
        RetrofitClient.youtubeService

    @Provides
    @Singleton
    fun provideVideoService(retrofit: Retrofit): VideoService {
        return retrofit.create(VideoService::class.java)
    }

    @Provides
    fun provideRecommendService(retrofit: Retrofit): RecommendService =
        retrofit.create(RecommendService::class.java)

    @Provides
    @Singleton
    fun provideTestService(retrofit: Retrofit): TestService {
        return retrofit.create(TestService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(service: AuthService): AuthRepository =
        AuthRepositoryImpl(service)

    @Provides
    @Singleton
    fun provideMyPageRepository(service: MyPageService): MyPageRepository =
        MyPageRepositoryImpl(service)

    @Provides
    @Singleton
    fun provideFriendRepository(service: FriendService): FriendRepository =
        FriendRepositoryImpl(service)

    @Provides
    @Singleton
    fun provideVideoRepository(service: VideoService): VideoRepository =
        VideoRepositoryImpl(service)

    @Provides
    fun provideRecommendRepository(recommendService: RecommendService): RecommendRepository =
        RecommendRepositoryImpl(recommendService)

    @Provides
    fun provideShadowingRepository(shadowingService: ShadowingService): ShadowingRepository =
        ShadowingRepositoryImpl(shadowingService)

    @Provides
    fun provideTestRepository(testService: TestService): TestRepository =
        TestRepositoryImpl(testService)

}
