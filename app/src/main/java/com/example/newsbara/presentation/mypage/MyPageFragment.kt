package com.example.newsbara.presentation.mypage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.newsbara.R
import com.example.newsbara.adapter.MyPageViewPagerAdapter
import com.example.newsbara.presentation.util.ResultState
import com.example.newsbara.presentation.login.LoginActivity
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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


        // MyPageFragment.kt
        lifecycleScope.launchWhenStarted {
            myPageViewModel.myPageInfo.collect { info ->
                info?.let {
                    pointsText.text = it.point.toString()
                    badgeText.text = it.badgeName ?:"No Badge"
                    nameText.text = it.name

                    Glide.with(this@MyPageFragment)
                        .load(it.profileImg)
                        .placeholder(R.drawable.ic_avatat)
                        .circleCrop()
                        .into(imageProfile)
                }
            }
        }

        val editButton = view.findViewById<ImageView>(R.id.profileImg)
        editButton.setOnClickListener {
            showEditNameDialog()
        }

        val backButton = view.findViewById<ImageButton>(R.id.myBackButton)
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val logoutButton = view.findViewById<Button>(R.id.btnLogout)
        logoutButton.setOnClickListener {
            myPageViewModel.logout()
        }

        // ✅ 로그아웃 상태 observe
        lifecycleScope.launchWhenStarted {
            myPageViewModel.logoutResult.collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        Toast.makeText(requireContext(), "로그아웃 중...", Toast.LENGTH_SHORT).show()
                    }

                    is ResultState.Success -> {
                        Toast.makeText(requireContext(), "로그아웃 완료", Toast.LENGTH_SHORT).show()
                        val intent = Intent(requireContext(), LoginActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }

                    is ResultState.Failure -> {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }

        // ✅ 닉네임 변경 결과 Observe
        lifecycleScope.launchWhenStarted {
            myPageViewModel.nameUpdateResult.collect { result ->
                when (result) {
                    is ResultState.Success -> {
                        Toast.makeText(requireContext(), "닉네임 변경 성공", Toast.LENGTH_SHORT).show()
                    }

                    is ResultState.Failure -> {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }
        myPageViewModel.fetchMyPageInfo()
    }

    private fun showEditNameDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_name, null)
        val editNickname = dialogView.findViewById<EditText>(R.id.editNickname)
        val btnConfirm = dialogView.findViewById<TextView>(R.id.btnConfirm)
        val btnCancel = dialogView.findViewById<TextView>(R.id.btnCancel)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnConfirm.setOnClickListener {
            val newName = editNickname.text.toString()
            if (newName.isNotBlank()) {
                myPageViewModel.updateName(newName)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "닉네임을 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }
}



