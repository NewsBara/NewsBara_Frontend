package com.example.newsbara

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newsbara.data.SubtitleLine
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import java.util.Timer
import java.util.TimerTask

class VideoActivity : AppCompatActivity() {

    private lateinit var youtubePlayerView: YouTubePlayerView
    private var subtitleTimer: Timer? = null
    private var currentTimeSec: Float = 0f

    private val subtitleList: List<SubtitleLine> by lazy {
        parseSrtToSubtitles(mockSrtText)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        val videoId = intent.getStringExtra("VIDEO_ID") ?: run {
            Toast.makeText(this, "영상 ID가 없습니다", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val title = intent.getStringExtra("VIDEO_TITLE") ?: "Untitled"
        Log.d("VideoActivity", "Received title: $title")

        youtubePlayerView = findViewById(R.id.youtubePlayerView)
        lifecycle.addObserver(youtubePlayerView)

        val titleTextView = findViewById<TextView>(R.id.videoTitle)
        titleTextView.text = title
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }
        val overlaySubtitleView = findViewById<TextView>(R.id.overlaySubtitle)
        val fullSubtitleTextView = findViewById<TextView>(R.id.subtitleText)

        fullSubtitleTextView.text = subtitleList.joinToString("\n\n") { it.text }


        youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {

            override fun onReady(youTubePlayer: YouTubePlayer) {
                // 유튜브 영상이 준비되면 실행됨
                youTubePlayer.cueVideo(videoId.toString(), 0f)
                // 특정 영상ID를 0초부터 큐잉

                youTubePlayer.addListener(object : AbstractYouTubePlayerListener() {
                    override fun onCurrentSecond(player: YouTubePlayer, second: Float) {
                        // 재생 중일때 현재 시간을 계속해서 추적
                        currentTimeSec = second
                    }
                })


                subtitleTimer = Timer()
                subtitleTimer?.scheduleAtFixedRate(object : TimerTask() {
                    override fun run() {
                        val currentTimeMs = (currentTimeSec * 1000).toLong()
                        val subtitle = subtitleList.find {
                            currentTimeMs in (it.startTime * 1000).toLong()..(it.endTime * 1000).toLong()
                        }

                        runOnUiThread {
                            overlaySubtitleView.text = subtitle?.text ?: ""
                        }
                    }
                }, 0, 500)
            }
        })
    }

    override fun onDestroy() {
        subtitleTimer?.cancel()
        subtitleTimer = null
        super.onDestroy()
    }

    private val mockSrtText = """
        1
        00:00:01,000 --> 00:00:03,000
        안녕 나는 김민지

        2
        00:00:04,000 --> 00:00:06,000
        안녕 나는 김여민

        3
        00:00:07,500 --> 00:00:10,000
        안녕 나는 전승은
    """.trimIndent()

    private fun parseSrtToSubtitles(srt: String): List<SubtitleLine> {
        val subtitleList = mutableListOf<SubtitleLine>()
        val blocks = srt.trim().split("\n\n")

        for (block in blocks) {
            val lines = block.split("\n")
            if (lines.size >= 3) {
                val timeLine = lines[1]
                val (start, end) = timeLine.split(" --> ").map { parseSrtTime(it) }
                val text = lines.subList(2, lines.size).joinToString("\n")
                subtitleList.add(SubtitleLine(start, end, text))
            }
        }
        return subtitleList
    }

    private fun parseSrtTime(timeStr: String): Double {
        val parts = timeStr.split(":", ",")
        val hours = parts[0].toInt()
        val minutes = parts[1].toInt()
        val seconds = parts[2].toInt()
        val millis = parts[3].toInt()
        return hours * 3600 + minutes * 60 + seconds + millis / 1000.0
    }
}
