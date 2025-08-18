package com.example.newsbara.presentation.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsbara.presentation.mypage.MyPageFragment
import com.example.newsbara.R
import com.example.newsbara.presentation.video.VideoActivity
import com.example.newsbara.adapter.VideoSectionAdapter
import com.example.newsbara.data.model.history.HistoryItem
import com.example.newsbara.presentation.mypage.MyPageViewModel
import com.example.newsbara.presentation.mypage.stats.StatsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val viewModel: HomeViewModel by viewModels()
    private val statsViewModel: StatsViewModel by viewModels()
    private val myPageViewModel: MyPageViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        myPageViewModel.fetchMyPageInfo()
        val profileButton: ImageView = findViewById(R.id.profileButton)


        lifecycleScope.launchWhenStarted {
            myPageViewModel.myPageInfo.collect { info ->
                info?.let {
                    Glide.with(this@HomeActivity)
                        .load(it.profileImg)
                        .circleCrop()
                        .placeholder(R.drawable.ic_avatat)
                        .into(profileButton)
                }
            }
        }

        profileButton.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, MyPageFragment())
                .addToBackStack(null)
                .commit()
        }

        setupRecyclerView()
        viewModel.fetchAllSections()

        lifecycleScope.launchWhenStarted {
            viewModel.videoSections.collect { sections ->
                recyclerView.adapter = VideoSectionAdapter(sections) { video ->
                    handleVideoClick(video)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.sectionRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun handleVideoClick(video: HistoryItem) {

        statsViewModel.saveWatchedHistory(video) { success ->
            if (success) {
                Log.d("HomeActivity", "✅ saveHistory 완료: ${video.videoId}")
            } else {
                Log.e("HomeActivity", "❌ saveHistory 실패")
            }
        }

        val intent = Intent(this, VideoActivity::class.java).apply {
            putExtra("videoId", video.videoId)
            putExtra("videoTitle", video.title)
        }
        startActivity(intent)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}
