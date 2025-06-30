package com.example.newsbara.presentation.mypage

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsbara.SharedViewModel
import com.example.newsbara.adapter.FriendAdapter
import com.example.newsbara.data.model.Friend
import com.example.newsbara.databinding.FragmentAddFriendBinding

class AddFriendFragment : Fragment() {

    private var _binding: FragmentAddFriendBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: FriendAdapter
    private val viewModel: SharedViewModel by activityViewModels()


    // 전체 친구 더미 리스트
    private val allFriends = listOf(
        Friend("1", "kim minji", "https://example.com/avatar1.jpg", 2569, 1),
        Friend("2", "lee soojin", "https://example.com/avatar2.jpg", 1800, 2),
        Friend("3", "park jimin", "https://example.com/avatar3.jpg", 900, 1)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddFriendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewModel: SharedViewModel by activityViewModels() // 여기로 수정

        adapter = FriendAdapter { friend ->
            viewModel.addFriend(friend)
            Toast.makeText(requireContext(), "${friend.name} 추가됨!", Toast.LENGTH_SHORT).show()
        }

        binding.friendRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.friendRecyclerView.adapter = adapter

        adapter.submitList(emptyList()) // 초기 빈 리스트

        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val keyword = s?.toString()?.trim().orEmpty()

                val currentFriends = viewModel.friends.value.orEmpty() // 바로 가져오기

                val filtered = allFriends.filter { friend ->
                    friend.name.contains(keyword, ignoreCase = true) &&
                            currentFriends.none { it.id == friend.id }
                }

                adapter.submitList(filtered)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

}