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
import com.example.newsbara.data.model.youtube.VideoSection
import com.example.newsbara.presentation.mypage.MyPageViewModel
import com.example.newsbara.network.RetrofitClient
import com.example.newsbara.presentation.mypage.stats.StatsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.Serializable

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    private val apiKey = "AIzaSyBh59zbA3ChdijyhZsvuhw5a-H5agDqslg"

    private val channels = mapOf(
        "BBC" to "UC16niRr50-MSBwiO3YDb3RA",
        "CNN" to "UCupvZG-5ko_eiXAupbDfxWw"
    )

    private val statsViewModel: StatsViewModel by viewModels()
    private val myPageViewModel: MyPageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // ✅ 실제 마이페이지 정보 fetch
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
        fetchVideoSections()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.sectionRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchVideoSections() {
        lifecycleScope.launch {
            try {
                val sectionList = channels.map { (channelName, channelId) ->
                    val response = RetrofitClient.youtubeService.searchVideosByChannel(
                        channelId = channelId,
                        query = "",
                        apiKey = apiKey
                    )

                    val videos = response.items
                        .filter { it.id.videoId != null }
                        .map {
                            HistoryItem(
                                id = 0,
                                videoId = it.id.videoId!!,
                                title = it.snippet.title,
                                thumbnail = it.snippet.thumbnails.medium.url,
                                channel = channelName,
                                length = "00:00:00",
                                category = channelName,
                                status = "WATCHED",
                                createdAt = ""
                            )
                        }

                    VideoSection(
                        categoryTitle = channelName,
                        videos = videos
                    )
                }

                recyclerView.adapter = VideoSectionAdapter(sectionList) { video ->
                    handleVideoClick(video)
                }

            } catch (e: Exception) {
                Log.e("API_ERROR", "유튜브 API 요청 실패", e)
                Toast.makeText(this@HomeActivity, "API 요청 실패: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun handleVideoClick(video: HistoryItem) {
        val subtitles = parseSrtToSubtitles(mockSrtText)

        // ✅ 1. 학습 기록 저장 (status = "WATCHED")
        lifecycleScope.launch {
            statsViewModel.saveHistory(
                SaveHistoryRequest(
                    videoId = video.videoId,
                    title = video.title,
                    thumbnail = video.thumbnail,
                    channel = video.channel,
                    length = video.length,
                    category = video.category,
                    status = "WATCHED"
                )
            ) { success ->
                if (success) {
                    Log.d("HomeActivity", "✅ saveHistory 완료: ${video.videoId}")
                } else {
                    Log.e("HomeActivity", "❌ saveHistory 실패")
                }
            }

        }

        // ✅ 2. VideoActivity로 이동
        val intent = Intent(this, VideoActivity::class.java).apply {
            putExtra("videoId", video.videoId)
            putExtra("videoTitle", video.title)
            putExtra("subtitleList", subtitles as Serializable)
        }
        Log.d("HomeActivity", "🎬 videoId 보내는 값: ${video.videoId}")
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
