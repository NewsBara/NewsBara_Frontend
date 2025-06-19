package com.example.newsbara

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsbara.adapter.VideoSectionAdapter
import com.example.newsbara.data.HistoryItem
import com.example.newsbara.data.SubtitleLine
import com.example.newsbara.data.VideoItem
import com.example.newsbara.data.VideoSection
import com.example.newsbara.retrofit.RetrofitClient
import kotlinx.coroutines.launch
import androidx.fragment.app.Fragment



class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val apiKey = "AIzaSyBh59zbA3ChdijyhZsvuhw5a-H5agDqslg"

    private val channels = mapOf(
        "BBC" to "UC16niRr50-MSBwiO3YDb3RA",
        "CNN" to "UCupvZG-5ko_eiXAupbDfxWw"
    )

    private lateinit var viewModel: SharedViewModel  // 선언만

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        viewModel = (application as MyApp).sharedViewModel


        val profileButton: ImageView = findViewById(R.id.profileButton)
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
                                id = 0, // 로컬 생성 시 기본값 (서버 저장 시 갱신)
                                videoId = it.id.videoId!!,
                                title = it.snippet.title,
                                thumbnail = it.snippet.thumbnails.medium.url,
                                channel = channelName,
                                length = "00:00:00", // 현재 API에서는 영상 길이 없음
                                category = channelName,
                                status = "WATCHED",
                                createdAt = "" // 선택사항
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
        viewModel.addToHistory(video)

        viewModel.setVideoData(
            id = video.videoId,
            title = video.title,
            subs = parseSrtToSubtitles(mockSrtText)
        )

        startActivity(Intent(this, VideoActivity::class.java))
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
            supportFragmentManager.popBackStack() // MyPageFragment만 제거
        } else {
            super.onBackPressed() // 앱 종료 or 이전 액티비티
        }
    }

}
