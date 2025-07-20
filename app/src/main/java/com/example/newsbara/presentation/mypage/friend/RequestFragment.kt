package com.example.newsbara.presentation.mypage.friend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsbara.R
import com.example.newsbara.SharedViewModel
import com.example.newsbara.adapter.RequestFriendAdapter

class RequestFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RequestFriendAdapter

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerRequest)

        adapter = RequestFriendAdapter(
            onAccept = { friend ->
                sharedViewModel.acceptFriendRequest(friend)
                Toast.makeText(requireContext(), "${friend.name} accepted", Toast.LENGTH_SHORT)
                    .show()
            },
            onReject = { friend ->
                sharedViewModel.rejectFriendRequest(friend)
                Toast.makeText(requireContext(), "${friend.name} rejected", Toast.LENGTH_SHORT)
                    .show()
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // LiveData 관찰해서 UI 업데이트
        sharedViewModel.friendRequests.observe(viewLifecycleOwner) { requestList ->
            adapter.submitList(requestList)
        }
    }
}