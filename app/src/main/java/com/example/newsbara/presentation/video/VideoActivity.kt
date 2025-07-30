package com.example.newsbara.presentation.video

import android.content.Intent
import android.os.Bundle
import android.os.Handler

import android.os.Looper
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.postDelayed
import com.example.newsbara.DefinitionProvider
import com.example.newsbara.R
import com.example.newsbara.SharedViewModel
import com.example.newsbara.data.model.youtube.SubtitleLine
import com.example.newsbara.data.model.youtube.getClickableSpannable
import com.example.newsbara.data.model.youtube.getHighlightedText
import com.example.newsbara.presentation.shadowing.ShadowingActivity
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable
import java.util.Timer
import java.util.TimerTask


@AndroidEntryPoint
class VideoActivity : AppCompatActivity() {

    private lateinit var youtubePlayerView: YouTubePlayerView
    private lateinit var fullSubtitleTextView: TextView
    private lateinit var overlaySubtitleView: TextView
    private var subtitleTimer: Timer? = null
    private var currentTimeSec: Float = 0f
    private var isTranslatedMode = false

    private lateinit var videoId: String
    private lateinit var videoTitle: String
    private lateinit var subtitleList: List<SubtitleLine>
    private val highlightWords = listOf("accelerating", "global", "urgent")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        youtubePlayerView = findViewById(R.id.youtubePlayerView)
        fullSubtitleTextView = findViewById(R.id.subtitleText)
        overlaySubtitleView = findViewById(R.id.overlaySubtitle)
        val titleTextView = findViewById<TextView>(R.id.videoTitle)

        lifecycle.addObserver(youtubePlayerView)

        videoId = intent.getStringExtra("videoId")?.trim().orEmpty()
        videoTitle = intent.getStringExtra("videoTitle").orEmpty()
        subtitleList = intent.getSerializableExtra("subtitleList") as? List<SubtitleLine> ?: emptyList()


        if (videoId.isEmpty()) {
            finish()
            return
        }

        // UI 세팅
        titleTextView.text = videoTitle
        fullSubtitleTextView.movementMethod = LinkMovementMethod.getInstance()
        updateFullSubtitle(subtitleList)

        // 자막 토글 버튼
        findViewById<Button>(R.id.toggleSubtitleModeButton).setOnClickListener {
            isTranslatedMode = !isTranslatedMode
            it as Button
            it.text = if (isTranslatedMode) "eng script" else "eng/kor script"
            updateFullSubtitle(subtitleList)
        }

        // 뒤로가기
        findViewById<ImageButton>(R.id.backButton).setOnClickListener { finish() }

        // 쉐도잉 이동
        findViewById<Button>(R.id.startShadowingButton).setOnClickListener {
            val intent = Intent(this, ShadowingActivity::class.java).apply {
                putExtra("subtitleList", subtitleList as Serializable)
            }
            startActivity(intent)
        }

        // 유튜브 플레이어 설정
        youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(player: YouTubePlayer) {
                player.cueVideo(videoId, 0f)
                player.addListener(object : AbstractYouTubePlayerListener() {
                    override fun onCurrentSecond(p: YouTubePlayer, second: Float) {
                        currentTimeSec = second
                    }
                })

                startSubtitleSyncTimer()
            }
        })
    }

    private fun updateFullSubtitle(subtitles: List<SubtitleLine>) {
        val spannableBuilder = SpannableStringBuilder()
        for (line in subtitles) {
            if (isTranslatedMode) {
                val spannable = line.getClickableSpannable(
                    highlightWords = highlightWords,
                    context = this,
                    anchorTextView = fullSubtitleTextView,
                    onDefinitionFetch = { word -> DefinitionProvider.getDefinition(word) }
                )
                spannableBuilder.append(spannable).append("\n")
            } else {
                val engLine = line.text.lineSequence().firstOrNull()?.trim().orEmpty()
                spannableBuilder.append(engLine).append("\n\n")
            }
        }
        fullSubtitleTextView.text = spannableBuilder
    }

    private fun startSubtitleSyncTimer() {
        subtitleTimer = Timer()
        subtitleTimer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val currentMs = (currentTimeSec * 1000).toLong()
                val currentSubtitle = subtitleList.find {
                    currentMs in (it.startTime * 1000).toLong()..(it.endTime * 1000).toLong()
                }
                val highlighted = currentSubtitle?.getHighlightedText(highlightWords) ?: ""
                runOnUiThread {
                    overlaySubtitleView.text = Html.fromHtml(highlighted, Html.FROM_HTML_MODE_COMPACT)
                }
            }
        }, 0, 500)
    }

    override fun onDestroy() {
        subtitleTimer?.cancel()
        subtitleTimer = null
        super.onDestroy()
    }
}
