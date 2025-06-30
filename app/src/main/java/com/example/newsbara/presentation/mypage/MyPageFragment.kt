package com.example.newsbara.presentation.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.newsbara.R
import com.example.newsbara.adapter.MyPageViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MyPageFragment : Fragment() {
    private val myPageViewModel: MyPageViewModel by viewModels()

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: MyPageViewPagerAdapter

    private lateinit var pointsText: TextView
    private lateinit var badgeText: TextView
    private lateinit var nameText: TextView
    private lateinit var imageProfile: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mypage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

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


        myPageViewModel.myPageInfo.observe(viewLifecycleOwner) { info ->
            pointsText.text = info.point.toString()
            badgeText.text = info.badgeName
            nameText.text = info.name

            Glide.with(this)
                .load(info.profileImg)
                .placeholder(R.drawable.ic_avatat)
                .circleCrop()
                .into(imageProfile)
        }

        val editButton = view.findViewById<ImageView>(R.id.profileImg)
        editButton.setOnClickListener {
            showEditNameDialog()
        }

        val backButton = view.findViewById<ImageButton>(R.id.myBackButton)
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun showEditNameDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_name, null)
        val editNickname = dialogView.findViewById<EditText>(R.id.editNickname)
        val btnConfirm = dialogView.findViewById<TextView>(R.id.btnConfirm)
        val btnCancel = dialogView.findViewById<TextView>(R.id.btnCancel)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            val newName = editNickname.text.toString()
            if (newName.isNotBlank()) {
                // 기존 info 가져와서 이름만 바꿔치기
                myPageViewModel.myPageInfo.value?.let { currentInfo ->
                    val updatedInfo = currentInfo.copy(name = newName)
                    myPageViewModel.setMyPageInfo(updatedInfo)
                }
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "닉네임을 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

}

