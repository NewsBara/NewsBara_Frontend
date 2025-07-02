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
import okhttp3.MultipartBody


interface MyPageRepository {
    suspend fun getMyPageInfo(): MyPageInfo
    suspend fun getHistory(): List<HistoryItem>
    suspend fun saveHistory(request: SaveHistoryRequest): HistoryItem
    suspend fun getBadge(): BadgeInfo
    suspend fun updatePoint(request: PointRequest): PointResult
    suspend fun updateName(request: UpdateNameRequest): UpdateNameResponse
    suspend fun updateProfileImage(file: MultipartBody.Part): UpdateProfileImageResponse

}
