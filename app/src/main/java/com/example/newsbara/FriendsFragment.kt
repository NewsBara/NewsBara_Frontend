package com.example.newsbara

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsbara.adapter.FriendAdapter
import com.example.newsbara.data.Friend

class FriendsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FriendAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.recyclerFriends)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = FriendAdapter()
        recyclerView.adapter = adapter

        // ✅ 지금은 더미데이터로 테스트
        val dummyList = listOf(
            Friend("Davis Curtis", "https://example.com/davis.jpg", 2569, R.drawable.badge_gold),
            Friend("Alena Donin", "https://example.com/alena.jpg", 1469, R.drawable.badge_silver),
            Friend("Craig Gouse", "https://example.com/craig.jpg", 1053, R.drawable.badge_bronze),
            Friend("Madelyn Dias", "https://example.com/madelyn.jpg", 590, R.drawable.badge_level2),
            Friend("Zain Vaccaro", "https://example.com/zain.jpg", 448, R.drawable.badge_level1)
        )

        adapter.submitList(dummyList)
    }
}

