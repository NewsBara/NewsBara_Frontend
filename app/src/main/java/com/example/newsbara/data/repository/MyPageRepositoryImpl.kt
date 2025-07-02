package com.example.newsbara.data.repository


import com.example.newsbara.data.model.history.HistoryItem
import com.example.newsbara.data.model.history.SaveHistoryRequest
import com.example.newsbara.data.model.mypage.BadgeInfo
import com.example.newsbara.data.model.mypage.MyPageInfo
import com.example.newsbara.data.model.mypage.PointRequest
import com.example.newsbara.data.model.mypage.PointResult
import com.example.newsbara.data.model.mypage.UpdateNameRequest
import com.example.newsbara.data.model.mypage.UpdateNameResponse
import com.example.newsbara.data.model.mypage.UpdateProfileImageResponse
import com.example.newsbara.data.service.MyPageService
import com.example.newsbara.domain.repository.MyPageRepository
import okhttp3.MultipartBody
import javax.inject.Inject


class MyPageRepositoryImpl @Inject constructor(
    private val myPageService: MyPageService
) : MyPageRepository {

    override suspend fun getMyPageInfo(): MyPageInfo {
        return myPageService.getMyPageInfo().body()?.result ?: throw Exception("데이터 없음")
    }

    override suspend fun getHistory(): List<HistoryItem> {
        return myPageService.getHistory().body()?.result ?: emptyList()
    }

    override suspend fun saveHistory(request: SaveHistoryRequest): HistoryItem {
        return myPageService.saveHistory(request).body()?.result ?: throw Exception("데이터 없음")
    }

    override suspend fun getBadge(): BadgeInfo {
        return myPageService.getBadge().body()?.result ?: throw Exception("데이터 없음")
    }

    override suspend fun updatePoint(request: PointRequest): PointResult {
        return myPageService.updatePoint(request).body()?.result ?: throw Exception("데이터 없음")
    }

    override suspend fun updateName(request: UpdateNameRequest): UpdateNameResponse {
        val response = myPageService.updateName(request)
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null && body.isSuccess) {
                return body.result
            } else {
                throw Exception("서버 오류: ${body?.message ?: "응답 없음"}")
            }
        } else {
            throw Exception("HTTP 오류: ${response.code()}")
        }
    }


    override suspend fun updateProfileImage(file: MultipartBody.Part): UpdateProfileImageResponse {
        return myPageService.updateProfileImage(file).body()?.result ?: throw Exception("데이터 없음")
    }

}
