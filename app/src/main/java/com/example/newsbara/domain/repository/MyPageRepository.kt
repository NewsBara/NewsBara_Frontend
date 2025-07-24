package com.example.newsbara.domain.repository

import com.example.newsbara.data.model.history.HistoryItem
import com.example.newsbara.data.model.history.SaveHistoryRequest
import com.example.newsbara.data.model.mypage.BadgeInfo
import com.example.newsbara.data.model.mypage.MyPageInfo
import com.example.newsbara.data.model.mypage.PointRequest
import com.example.newsbara.data.model.mypage.PointResult
import com.example.newsbara.data.model.mypage.UpdateNameRequest
import com.example.newsbara.data.model.mypage.UpdateNameResponse
import com.example.newsbara.data.model.mypage.UpdateProfileImageResponse
import com.example.newsbara.presentation.util.ResultState
import okhttp3.MultipartBody


interface MyPageRepository {
    suspend fun getMyPageInfo(): ResultState<MyPageInfo>
    suspend fun getHistory(): ResultState<List<HistoryItem>>
    suspend fun saveHistory(request: SaveHistoryRequest): ResultState<HistoryItem>
    suspend fun getBadge(): ResultState<BadgeInfo>
    suspend fun updatePoint(request: PointRequest): ResultState<PointResult>
    suspend fun updateName(request: UpdateNameRequest): ResultState<UpdateNameResponse>
    suspend fun updateProfileImage(file: MultipartBody.Part): ResultState<UpdateProfileImageResponse>
}
