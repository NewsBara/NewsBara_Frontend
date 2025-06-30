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

class AddFriendAdapter(
    private val onAddClick: (Friend) -> Unit
) : RecyclerView.Adapter<AddFriendAdapter.FriendViewHolder>() {

    private var friendList: List<Friend> = emptyList()

    fun submitList(list: List<Friend>) {
        friendList = list
        notifyDataSetChanged()
    }

    inner class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageProfile: ImageView = itemView.findViewById(R.id.imageProfile)
        private val textName: TextView = itemView.findViewById(R.id.textName)
        private val textPoints: TextView = itemView.findViewById(R.id.textPoints)
        private val imageBadge: ImageView = itemView.findViewById(R.id.imageBadge)
        private val btnAdd: Button = itemView.findViewById(R.id.btnAdd)

        fun bind(friend: Friend) {
            textName.text = friend.name
            textPoints.text = "${friend.points} points"
            Glide.with(itemView)
                .load(friend.profileUrl)
                .placeholder(R.drawable.placeholder)
                .into(imageProfile)
            imageBadge.setImageResource(R.drawable.ic_lv1) // 레벨에 따라 조정 가능

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
