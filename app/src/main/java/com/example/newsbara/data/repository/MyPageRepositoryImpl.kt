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
import com.example.newsbara.presentation.util.ResultState
import okhttp3.MultipartBody
import javax.inject.Inject


class MyPageRepositoryImpl @Inject constructor(
    private val myPageService: MyPageService
) : MyPageRepository {

    override suspend fun getMyPageInfo(): ResultState<MyPageInfo> {
        return try {
            val response = myPageService.getMyPageInfo()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.isSuccess) {
                    ResultState.Success(body.result)
                } else {
                    ResultState.Failure(body?.message ?: "응답 오류")
                }
            } else {
                ResultState.Failure("HTTP 오류: ${response.code()}")
            }
        } catch (e: Exception) {
            ResultState.Failure("예외 발생: ${e.localizedMessage ?: "알 수 없음"}")
        }
    }


    override suspend fun getHistory(): ResultState<List<HistoryItem>> {
        return try {
            val response = myPageService.getHistory()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.isSuccess) {
                    ResultState.Success(body.result)
                } else {
                    ResultState.Failure(body?.message ?: "응답 오류")
                }
            } else {
                ResultState.Failure("HTTP 오류: ${response.code()}")
            }
        } catch (e: Exception) {
            ResultState.Failure("예외 발생: ${e.localizedMessage ?: "알 수 없음"}")
        }
    }

    override suspend fun saveHistory(request: SaveHistoryRequest): ResultState<HistoryItem> {
        return try {
            val response = myPageService.saveHistory(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.isSuccess) {
                    ResultState.Success(body.result)
                } else {
                    ResultState.Failure(body?.message ?: "응답 오류")
                }
            } else {
                ResultState.Failure("HTTP 오류: ${response.code()}")
            }
        } catch (e: Exception) {
            ResultState.Failure("예외 발생: ${e.localizedMessage ?: "알 수 없음"}")
        }
    }

    override suspend fun getBadge(): ResultState<BadgeInfo> {
        return try {
            val response = myPageService.getBadge()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.isSuccess) {
                    ResultState.Success(body.result)
                } else {
                    ResultState.Failure(body?.message ?: "응답 오류")
                }
            } else {
                ResultState.Failure("HTTP 오류: ${response.code()}")
            }
        } catch (e: Exception) {
            ResultState.Failure("예외 발생: ${e.localizedMessage ?: "알 수 없음"}")
        }
    }

    override suspend fun updatePoint(request: PointRequest): ResultState<PointResult> {
        return try {
            val response = myPageService.updatePoint(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.isSuccess) {
                    ResultState.Success(body.result)
                } else {
                    ResultState.Failure(body?.message ?: "응답 오류")
                }
            } else {
                ResultState.Failure("HTTP 오류: ${response.code()}")
            }
        } catch (e: Exception) {
            ResultState.Failure("예외 발생: ${e.localizedMessage ?: "알 수 없음"}")
        }
    }

    override suspend fun updateName(request: UpdateNameRequest): ResultState<UpdateNameResponse> {
        return try {
            val response = myPageService.updateName(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.isSuccess) {
                    ResultState.Success(body.result)
                } else {
                    ResultState.Failure(body?.message ?: "응답 오류")
                }
            } else {
                ResultState.Failure("HTTP 오류: ${response.code()}")
            }
        } catch (e: Exception) {
            ResultState.Failure("예외 발생: ${e.localizedMessage ?: "알 수 없음"}")
        }
    }

    override suspend fun updateProfileImage(file: MultipartBody.Part): ResultState<UpdateProfileImageResponse> {
        return try {
            val response = myPageService.updateProfileImage(file)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.isSuccess) {
                    ResultState.Success(body.result)
                } else {
                    ResultState.Failure(body?.message ?: "응답 오류")
                }
            } else {
                ResultState.Failure("HTTP 오류: ${response.code()}")
            }
        } catch (e: Exception) {
            ResultState.Failure("예외 발생: ${e.localizedMessage ?: "알 수 없음"}")
        }
    }


}
