package com.example.newsbara

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
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
    private lateinit var imageProfile: ImageView

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
        imageProfile = view.findViewById(R.id.ivProfile)

        adapter = MyPageViewPagerAdapter(this)
        viewPager.adapter = adapter

        val tabTitles = listOf("Stats", "Badge", "Friends")
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()


        viewModel.myPageInfo.observe(viewLifecycleOwner) { info ->
            pointsText.text = info.point.toString()
            badgeText.text = info.badgeName
            nameText.text = info.name

            Glide.with(this)
                .load(info.profileImg)
                .placeholder(R.drawable.ic_avatat)
                .circleCrop()
                .into(imageProfile)
        }
    }
}

