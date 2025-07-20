package com.example.newsbara.presentation.mypage.friend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsbara.SharedViewModel
import com.example.newsbara.adapter.RankingFriendAdapter
import com.example.newsbara.databinding.FragmentRankingBinding

class RankingFragment : Fragment() {

    private lateinit var binding: FragmentRankingBinding
    private lateinit var adapter: RankingFriendAdapter

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRankingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = RankingFriendAdapter()
        binding.recyclerFriends.layoutManager = LinearLayoutManager(context)
        binding.recyclerFriends.adapter = adapter

        sharedViewModel.friends.observe(viewLifecycleOwner) { friends ->
            // 포인트 내림차순 정렬 후 리스트 적용
            val sorted = friends.sortedByDescending { it.points }
            adapter.submitList(sorted)
        }
    }
}