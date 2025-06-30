package com.example.newsbara.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.newsbara.presentation.mypage.BadgeFragment
import com.example.newsbara.presentation.mypage.FriendsFragment
import com.example.newsbara.presentation.mypage.StatsFragment

class MyPageViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> StatsFragment()
            1 -> BadgeFragment()
            2 -> FriendsFragment()
            else -> throw IndexOutOfBoundsException("Invalid tab position")
        }
    }
}
