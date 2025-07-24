package com.example.newsbara.data.repository

import com.example.newsbara.data.model.friends.FollowResult
import com.example.newsbara.data.model.friends.FriendDecisionRequest
import com.example.newsbara.data.model.friends.FriendDecisionResponse
import com.example.newsbara.data.model.friends.FriendListItem
import com.example.newsbara.data.model.friends.FriendRequestItem
import com.example.newsbara.data.model.friends.FriendSearchResponse
import com.example.newsbara.data.service.FriendService
import com.example.newsbara.domain.repository.FriendRepository
import com.example.newsbara.presentation.util.ResultState
import javax.inject.Inject

class FriendRepositoryImpl @Inject constructor(
    private val friendService: FriendService
) : FriendRepository {

    override suspend fun sendFriendRequest(userId: Int): ResultState<FollowResult> {
        return try {
            val response = friendService.sendFriendRequest(userId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.isSuccess) {
                    ResultState.Success(body.result)
                } else {
                    ResultState.Failure(body?.message ?: "알 수 없는 오류")
                }
            } else {
                ResultState.Failure("요청 실패: ${response.code()}")
            }
        } catch (e: Exception) {
            ResultState.Failure("예외 발생: ${e.message}")
        }
    }

    override suspend fun decideFollowRequest(
        requestId: Int,
        decision: FriendDecisionRequest
    ): ResultState<FriendDecisionResponse> {
        return try {
            val response = friendService.decideFollowRequest(requestId, decision)
            if (response.isSuccessful && response.body()?.isSuccess == true) {
                ResultState.Success(response.body()!!.result)
            } else {
                ResultState.Failure(response.body()?.message ?: "알 수 없는 오류")
            }
        } catch (e: Exception) {
            ResultState.Failure(e.message ?: "네트워크 오류 발생")
        }
    }

    override suspend fun searchUsers(name: String): ResultState<List<FriendSearchResponse>> {
        return try {
            val response = friendService.searchUsers(name)
            if (response.isSuccessful && response.body()?.isSuccess == true) {
                ResultState.Success(response.body()!!.result)
            } else {
                ResultState.Failure(response.body()?.message ?: "서버 오류")
            }
        } catch (e: Exception) {
            ResultState.Failure(e.message ?: "네트워크 오류")
        }
    }

    override suspend fun getFriendRequests(): ResultState<List<FriendRequestItem>> {
        return try {
            val response = friendService.getFriendRequests()
            if (response.isSuccessful && response.body()?.isSuccess == true) {
                ResultState.Success(response.body()!!.result)
            } else {
                ResultState.Failure(response.body()?.message ?: "서버 오류")
            }
        } catch (e: Exception) {
            ResultState.Failure(e.message ?: "네트워크 오류")
        }
    }

    override suspend fun getFriends(): ResultState<List<FriendListItem>> {
        return try {
            val response = friendService.getFriends()
            if (response.isSuccessful && response.body()?.isSuccess == true) {
                ResultState.Success(response.body()!!.result)
            } else {
                ResultState.Failure(response.body()?.message ?: "서버 오류")
            }
        } catch (e: Exception) {
            ResultState.Failure(e.message ?: "네트워크 오류")
        }
    }
}
