package com.example.newsbara.domain.repository

import com.example.newsbara.data.model.friends.FollowResult
import com.example.newsbara.data.model.friends.FriendDecisionRequest
import com.example.newsbara.data.model.friends.FriendDecisionResponse
import com.example.newsbara.data.model.friends.FriendListItem
import com.example.newsbara.data.model.friends.FriendRequestItem
import com.example.newsbara.data.model.friends.FriendSearchResponse
import com.example.newsbara.presentation.util.ResultState

interface FriendRepository {
    suspend fun sendFriendRequest(userId: Int): ResultState<FollowResult>
    suspend fun decideFollowRequest(requestId: Int, decision: FriendDecisionRequest): ResultState<FriendDecisionResponse>
    suspend fun searchUsers(name: String): ResultState<List<FriendSearchResponse>>
    suspend fun getFriendRequests(): ResultState<List<FriendRequestItem>>
    suspend fun getFriends(): ResultState<List<FriendListItem>>
}
