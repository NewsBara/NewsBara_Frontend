package com.example.newsbara.presentation.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsbara.presentation.mypage.MyPageFragment
import com.example.newsbara.R
import com.example.newsbara.SharedViewModel
import com.example.newsbara.presentation.video.VideoActivity
import com.example.newsbara.adapter.VideoSectionAdapter
import com.example.newsbara.data.model.history.HistoryItem
import com.example.newsbara.data.model.history.SaveHistoryRequest
import com.example.newsbara.data.model.youtube.SubtitleLine
import com.example.newsbara.data.model.youtube.VideoCategoryItem
import com.example.newsbara.data.model.youtube.VideoSection
import com.example.newsbara.di.YouTubeUtil.parseYouTubeDuration
import com.example.newsbara.domain.repository.YouTubeRepository
import com.example.newsbara.presentation.mypage.MyPageViewModel
import com.example.newsbara.network.RetrofitClient
import com.example.newsbara.presentation.mypage.stats.StatsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.Serializable
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private val viewModel: HomeViewModel by viewModels()
    private val statsViewModel: StatsViewModel by viewModels()
    private val myPageViewModel: MyPageViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView

    private val channels = mapOf(
        "BBC" to "UC16niRr50-MSBwiO3YDb3RA",
        "CNN" to "UCupvZG-5ko_eiXAupbDfxWw"
    )

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
        viewModel.fetchVideoSections(channels)
        Log.d("HomeActivity", "📡 채널 리스트: $channels")


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
        val subtitles = parseSrtToSubtitles(mockSrtText)

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
            putExtra("subtitleList", subtitles as Serializable)
        }
        startActivity(intent)
    }

    private val mockSrtText = """
        1
        00:00:01,000 --> 00:00:03,000
        Climate change is accelerating faster than expected.
        
        2
        00:00:04,000 --> 00:00:06,000
        Scientists warn that global temperatures could rise above 2 degrees.
        
        3
        00:00:07,000 --> 00:00:09,000
        The United Nations is urging urgent action.
        
        4
        00:00:09,500 --> 00:00:12,000
        Renewable energy is seen as a key solution to reduce emissions.
    """.trimIndent()

    private fun parseSrtToSubtitles(srt: String): List<SubtitleLine> {
        return srt.trim().split("\n\n").mapNotNull { block ->
            val lines = block.lines()
            if (lines.size >= 3) {
                val timeLine = lines[1]
                val (start, end) = timeLine.split(" --> ").map { parseSrtTime(it) }
                val text = lines.subList(2, lines.size).joinToString("\n")
                SubtitleLine(start, end, text)
            } else null
        }
    }

    private fun parseSrtTime(timeStr: String): Double {
        val parts = timeStr.split(":", ",")
        val (hours, minutes, seconds, millis) = parts.map { it.toInt() }
        return hours * 3600 + minutes * 60 + seconds + millis / 1000.0
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}
