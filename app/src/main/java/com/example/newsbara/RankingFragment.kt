package com.example.newsbara

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsbara.adapter.RankingFriendAdapter
import com.example.newsbara.data.Friend
import com.example.newsbara.databinding.FragmentRankingBinding

class RankingFragment : Fragment() {

    private lateinit var binding: FragmentRankingBinding
    private lateinit var adapter: RankingFriendAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRankingBinding.inflate(inflater, container, false)

        adapter = RankingFriendAdapter()
        binding.recyclerFriends.adapter = adapter
        binding.recyclerFriends.layoutManager = LinearLayoutManager(context)

        val dummyFriends = listOf(
            Friend("1", "kim minji", "https://example.com/avatar1.jpg", 2569, 3),
            Friend("2", "lee soojin", "https://example.com/avatar2.jpg", 2100, 2)
        )


        adapter.submitList(dummyFriends)

        return binding.root
    }
}
