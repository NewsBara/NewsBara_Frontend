package com.example.newsbara.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsbara.R
import com.example.newsbara.data.model.Friend

class RankingFriendAdapter : RecyclerView.Adapter<RankingFriendAdapter.FriendViewHolder>() {

    private var friendList: List<Friend> = emptyList()

    fun submitList(list: List<Friend>) {
        friendList = list
        notifyDataSetChanged()
    }

    inner class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textRank: TextView = itemView.findViewById(R.id.textRank)
        val imageProfile: ImageView = itemView.findViewById(R.id.imageProfile)
        val textName: TextView = itemView.findViewById(R.id.textName)
        val textPoints: TextView = itemView.findViewById(R.id.textPoints)
        val imageBadge: ImageView = itemView.findViewById(R.id.imageBadge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend, parent, false)
        return FriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friendList[position]

        // 순위 표시
        holder.textRank.text = "${position + 1}"

        holder.textName.text = friend.name
        holder.textPoints.text = "${friend.points} points"

        // 프로필 이미지
        Glide.with(holder.itemView)
            .load(friend.profileUrl)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.ic_error)
            .into(holder.imageProfile)

        // 뱃지 설정
        val badgeResId = when (friend.badgeLevel) {
            1 -> R.drawable.ic_lv1
            2 -> R.drawable.ic_lv2
            3 -> R.drawable.ic_lv3_
            4 -> R.drawable.ic_lv4
            5 -> R.drawable.ic_lv5
            else -> R.drawable.ic_lv1
        }
        holder.imageBadge.setImageResource(badgeResId)
    }

    override fun getItemCount(): Int = friendList.size
}
