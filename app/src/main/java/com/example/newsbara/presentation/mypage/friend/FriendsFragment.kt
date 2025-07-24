package com.example.newsbara.presentation.mypage.friend

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.newsbara.R
import com.example.newsbara.databinding.FragmentFriendsBinding
import com.example.newsbara.presentation.mypage.friend.RankingFragment
import com.example.newsbara.presentation.mypage.friend.RequestFragment

class FriendsFragment : Fragment() {

    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        childFragmentManager.beginTransaction()
            .replace(R.id.friendsContentFrame, RankingFragment())
            .commit()

        binding.friendsToggleGroup.setOnCheckedChangeListener { _, checkedId ->
            Log.d("FriendsToggle", "Checked ID: $checkedId")
            val selectedFragment = when (checkedId) {
                R.id.btnRanking -> RankingFragment()
                R.id.btnAdd -> AddFriendFragment()
                R.id.btnRequest -> RequestFragment()
                else -> null
            } as? Fragment

            selectedFragment?.let {
                childFragmentManager.beginTransaction()
                    .replace(R.id.friendsContentFrame, it)
                    .commit()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}