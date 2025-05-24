package com.example.newsbara

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsbara.adapter.VideoSectionAdapter
import com.example.newsbara.data.SubtitleLine
import com.example.newsbara.data.VideoItem
import com.example.newsbara.data.VideoSection
import com.example.newsbara.retrofit.RetrofitClient
import kotlinx.coroutines.launch
class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val apiKey = "AIzaSyBh59zbA3ChdijyhZsvuhw5a-H5agDqslg"
    private val channels = mapOf(
        "BBC" to "UC16niRr50-MSBwiO3YDb3RA",
        "CNN" to "UCupvZG-5ko_eiXAupbDfxWw"
    )

    private val viewModel by lazy {
        (application as MyApp).sharedViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

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
                                VideoItem(
                                    videoId = it.id.videoId!!,
                                    title = it.snippet.title,
                                    thumbnailUrl = it.snippet.thumbnails.medium.url
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
                    Toast.makeText(this@HomeActivity, "API 요청 실패: ${e.message}", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }

        private fun handleVideoClick(video: VideoItem) {
            // ✅ ViewModel에 데이터 전달
            viewModel.setVideoData(
                id = video.videoId,
                title = video.title,
                subs = parseSrtToSubtitles(mockSrtText)  // 임시 자막 (나중에 API 자막으로 대체 예정)
            )

            // ✅ 화면 전환 (데이터는 ViewModel로 전달했으므로 Intent는 비워둠)
            startActivity(Intent(this, VideoActivity::class.java))
        }

        private val mockSrtText = """
    1
    00:00:01,000 --> 00:00:03,000
    Climate change is accelerating faster than expected.
    기후 변화는 예상보다 더 빠르게 가속화되고 있습니다.

    2
    00:00:04,000 --> 00:00:06,000
    Scientists warn that global temperatures could rise above 2 degrees.
    과학자들은 지구 평균 기온이 2도 이상 오를 수 있다고 경고합니다.

    3
    00:00:07,000 --> 00:00:09,000
    The United Nations is urging urgent action.
    유엔은 시급한 조치를 촉구하고 있습니다.

    4
    00:00:09,500 --> 00:00:12,000
    Renewable energy is seen as a key solution to reduce emissions.
    재생 가능 에너지는 배출량을 줄이는 핵심 해결책으로 간주됩니다.
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
    }
