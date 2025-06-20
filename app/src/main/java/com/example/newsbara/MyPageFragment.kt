package com.example.newsbara

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.newsbara.adapter.MyPageViewPagerAdapter
import com.example.newsbara.data.MyPageInfo
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MyPageFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: MyPageViewPagerAdapter
    private lateinit var viewModel: SharedViewModel

    private lateinit var pointsText: TextView
    private lateinit var badgeText: TextView
    private lateinit var nameText: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mypage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = (requireActivity().application as MyApp).sharedViewModel

        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.viewPager)
        pointsText = view.findViewById(R.id.points)
        badgeText = view.findViewById(R.id.badge)
        nameText = view.findViewById(R.id.tvName)

        adapter = MyPageViewPagerAdapter(this)
        viewPager.adapter = adapter

        val tabTitles = listOf("Stats", "Badge", "Friends")
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        val dummyInfo = MyPageInfo(
            id = 1,
            email = "test@example.com",
            name = "김민지",
            badgeName = "Lv.2",
            point = 600,
            profileImg = null
        )
        viewModel.setMyPageInfo(dummyInfo)

        // LiveData 관찰
        viewModel.myPageInfo.observe(viewLifecycleOwner) { info ->
            pointsText.text = info.point.toString()
            badgeText.text = info.badgeName
            nameText.text = info.name
        }
    }
}

