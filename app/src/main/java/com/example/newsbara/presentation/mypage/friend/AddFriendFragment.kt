package com.example.newsbara.presentation.mypage.friend

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
import com.example.newsbara.adapter.AddFriendAdapter
import com.example.newsbara.adapter.FriendAdapter
import com.example.newsbara.data.model.Friend
import com.example.newsbara.databinding.FragmentAddFriendBinding

class AddFriendFragment : Fragment() {

    private var _binding: FragmentAddFriendBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: AddFriendAdapter
    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddFriendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = AddFriendAdapter(
            onAddClick = { user ->
                viewModel.sendFriendRequest(user.userId)
                Toast.makeText(
                    requireContext(),
                    "${user.userName}에게 친구 요청 보냈습니다!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        binding.friendRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.friendRecyclerView.adapter = adapter

        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val keyword = s?.toString()?.trim().orEmpty()
                if (keyword.isNotEmpty()) {
                    viewModel.searchUsers(keyword)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        viewModel.searchResults.observe(viewLifecycleOwner) { results ->
            adapter.submitList(results)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
