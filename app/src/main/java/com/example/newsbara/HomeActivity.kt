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
import com.example.newsbara.data.VideoItem
import com.example.newsbara.data.VideoSection
import com.example.newsbara.retrofit.RetrofitClient
import kotlinx.coroutines.launch
class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val apiKey = "AIzaSyBh59zbA3ChdijyhZsvuhw5a-H5agDqslg"
    private val categories = listOf("Science", "Politics", "Sports")
    private val channels = mapOf(
        "BBC" to "UC16niRr50-MSBwiO3YDb3RA",
        "CNN" to "UCupvZG-5ko_eiXAupbDfxWw"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerView = findViewById(R.id.sectionRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            try {
                val allowedChannels = setOf(
                    "UC16niRr50-MSBwiO3YDb3RA",
                    "UCupvZG-5ko_eiXAupbDfxWw"
                )

                val sectionList = mutableListOf<VideoSection>()

                for ((channelName, channelId) in channels) {
                    for (category in categories) {
                        val response = RetrofitClient.youtubeService.searchVideosByChannel(
                            channelId = channelId,
                            query = category,
                            apiKey = apiKey
                        )

                        val videos = response.items
                            .filter { it.id.videoId != null && it.snippet.channelId in allowedChannels }
                            .map {
                                VideoItem(
                                    videoId = it.id.videoId!!,
                                    title = it.snippet.title,
                                    thumbnailUrl = it.snippet.thumbnails.medium.url
                                )
                            }

                        sectionList.add(
                            VideoSection(
                                categoryTitle = "$channelName - $category",
                                videos = videos
                            )
                        )
                    }
                }

                recyclerView.adapter = VideoSectionAdapter(sectionList) { video ->
                    val intent = Intent(this@HomeActivity, VideoActivity::class.java)
                    intent.putExtra("VIDEO_ID", video.videoId)
                    intent.putExtra("VIDEO_TITLE", video.title)
                    startActivity(intent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("API_ERROR", "예외 발생", e)
                Toast.makeText(this@HomeActivity, "API 실패: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

    }
}
