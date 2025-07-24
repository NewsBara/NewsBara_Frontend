package com.example.newsbara.data.service

import com.example.newsbara.data.BaseResponse
import com.example.newsbara.data.model.friends.FollowResult
import com.example.newsbara.data.model.friends.FriendDecisionRequest
import com.example.newsbara.data.model.friends.FriendDecisionResponse
import com.example.newsbara.data.model.friends.FriendListItem
import com.example.newsbara.data.model.friends.FriendRequestItem
import com.example.newsbara.data.model.friends.FriendSearchResponse
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Query

interface FriendService {

    @POST("/api/follows/{userId}/add")
    suspend fun sendFriendRequest(
        @Path("userId") userId: Int
    ): Response<BaseResponse<FollowResult>>

    @PATCH("/api/follows/requests/{requestId}")
    suspend fun decideFollowRequest(
        @Path("requestId") requestId: Int,
        @Body decision: FriendDecisionRequest
    ): Response<BaseResponse<FriendDecisionResponse>>

    @GET("/api/follows/search")
    suspend fun searchUsers(
        @Query("name") name: String
    ): Response<BaseResponse<List<FriendSearchResponse>>>

    @GET("/api/follows/requests")
    suspend fun getFriendRequests(): Response<BaseResponse<List<FriendRequestItem>>>

    @GET("/api/follows/friends")
    suspend fun getFriends(): Response<BaseResponse<List<FriendListItem>>>

}
