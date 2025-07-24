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
import com.example.newsbara.data.model.friends.FriendSearchResponse

class AddFriendAdapter(
    private val onAddClick: (FriendSearchResponse) -> Unit
) : RecyclerView.Adapter<AddFriendAdapter.FriendViewHolder>() {

    private var friendList: List<FriendSearchResponse> = emptyList()

    fun submitList(list: List<FriendSearchResponse>) {
        friendList = list
        notifyDataSetChanged()
    }

    inner class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageProfile: ImageView = itemView.findViewById(R.id.imageProfile)
        private val textName: TextView = itemView.findViewById(R.id.textName)
        private val textPoints: TextView = itemView.findViewById(R.id.textPoints)
        private val btnAdd: Button = itemView.findViewById(R.id.btnAdd)

        fun bind(friend: FriendSearchResponse) {
            textName.text = friend.userName
            textPoints.text = if (friend.following) "이미 친구" else if (friend.pending) "대기 중" else ""
            Glide.with(itemView)
                .load(friend.profileImage)
                .placeholder(R.drawable.placeholder)
                .into(imageProfile)

            btnAdd.setOnClickListener {
                onAddClick(friend)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend_add, parent, false)
        return FriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bind(friendList[position])
    }

    override fun getItemCount(): Int = friendList.size
}
