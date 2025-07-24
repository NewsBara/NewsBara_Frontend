package com.example.newsbara.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsbara.R
import com.example.newsbara.data.model.Friend
import com.example.newsbara.data.model.friends.FriendRequestItem


class RequestFriendAdapter(
    private val onAccept: (FriendRequestItem) -> Unit,
    private val onReject: (FriendRequestItem) -> Unit
) : RecyclerView.Adapter<RequestFriendAdapter.RequestViewHolder>() {

    private var requestList: List<FriendRequestItem> = emptyList()

    fun submitList(list: List<FriendRequestItem>) {
        requestList = list
        notifyDataSetChanged()
    }

    inner class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageProfile: ImageView = itemView.findViewById(R.id.imageProfile)
        private val textName: TextView = itemView.findViewById(R.id.textName)
        private val textPoints: TextView = itemView.findViewById(R.id.textPoints)
        private val btnAccept: Button = itemView.findViewById(R.id.btnAccept)
        private val btnReject: Button = itemView.findViewById(R.id.btnReject)

        fun bind(friend: FriendRequestItem) {
            textName.text = friend.followerName
            textPoints.text = "${friend.followerPoint} points"

            Glide.with(itemView)
                .load(friend.followerProfileImage)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.ic_error)
                .into(imageProfile)

            btnAccept.setOnClickListener { onAccept(friend) }
            btnReject.setOnClickListener { onReject(friend) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend_request, parent, false)
        return RequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        holder.bind(requestList[position])
    }

    override fun getItemCount(): Int = requestList.size
}
