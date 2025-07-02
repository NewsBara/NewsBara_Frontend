package com.example.newsbara.di



import android.content.Context
import com.example.newsbara.BuildConfig
import com.example.newsbara.BuildConfig.BASE_URL
import com.example.newsbara.data.repository.AuthRepositoryImpl
import com.example.newsbara.data.repository.MyPageRepositoryImpl
import com.example.newsbara.data.service.AuthService
import com.example.newsbara.data.service.MyPageService
import com.example.newsbara.data.service.YouTubeApiService
import com.example.newsbara.domain.repository.AuthRepository
import com.example.newsbara.domain.repository.MyPageRepository
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
            .build()

    @Provides
    @Singleton
    fun provideApiRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // ✅ Retrofit 기반 서비스 주입
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
    fun provideYouTubeService(): YouTubeApiService =
        RetrofitClient.youtubeService // 그대로 사용 OK


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
