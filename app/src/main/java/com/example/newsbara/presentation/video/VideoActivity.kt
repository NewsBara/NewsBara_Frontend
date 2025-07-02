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
import java.util.Timer
import java.util.TimerTask



@AndroidEntryPoint
class VideoActivity : AppCompatActivity() {

    private lateinit var youtubePlayerView: YouTubePlayerView
    private var subtitleTimer: Timer? = null
    private var currentTimeSec: Float = 0f
    private var isTranslatedMode = false
    private lateinit var fullSubtitleTextView: TextView
    private lateinit var highlightWords: List<String>

    private lateinit var subtitleList: List<SubtitleLine>
    private lateinit var videoId: String
    private lateinit var videoTitle: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        youtubePlayerView = findViewById(R.id.youtubePlayerView)
        Log.d("YouTubePlayer", "📦 youtubePlayerView 객체: $youtubePlayerView")

        lifecycle.addObserver(youtubePlayerView)


        // ✅ Intent로부터 데이터 추출
        videoId = intent.getStringExtra("videoId")?.trim() ?: ""
        if (videoId.isNullOrEmpty()) {
            Log.e("VideoActivity", "❌ videoId가 null 또는 빈 값입니다.")
            finish()
            return
        }
        videoTitle = intent.getStringExtra("videoTitle") ?: ""
        Log.d("VideoActivity", "🎥 받은 videoId: $videoId")
        @Suppress("UNCHECKED_CAST")
        subtitleList =
            intent.getSerializableExtra("subtitleList") as? List<SubtitleLine> ?: emptyList()

        highlightWords = listOf("accelerating", "global", "urgent") // 필요한 경우 외부에서 받도록 변경

        fullSubtitleTextView = findViewById(R.id.subtitleText)

        val toggleSubButton = findViewById<Button>(R.id.toggleSubtitleModeButton)
        toggleSubButton.setOnClickListener {
            isTranslatedMode = !isTranslatedMode
            toggleSubButton.text = if (isTranslatedMode) "eng script" else "eng/kor script"
            updateFullSubtitle(subtitleList)
        }

        val titleTextView = findViewById<TextView>(R.id.videoTitle)
        val overlaySubtitleView = findViewById<TextView>(R.id.overlaySubtitle)
        fullSubtitleTextView.movementMethod = LinkMovementMethod.getInstance()

        titleTextView.text = videoTitle
        updateFullSubtitle(subtitleList)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener { finish() }

        val startShadowingButton = findViewById<Button>(R.id.startShadowingButton)
        startShadowingButton.setOnClickListener {
            val intent = Intent(this@VideoActivity, ShadowingActivity::class.java)
            startActivity(intent)
        }

        youtubePlayerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                youtubePlayerView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val width = youtubePlayerView.width
                val height = youtubePlayerView.height
                Log.d("YouTubePlayer", "📏 크기: ${width}x$height") // 디버깅용

                if (width > 0 && height > 0) {
                    youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                        override fun onReady(player: YouTubePlayer) {
                            Log.d("YouTubePlayer", "✅ onReady 호출됨")

                            Handler(Looper.getMainLooper()).postDelayed({
                                Log.d("YouTubePlayer", "▶ cueVideo 딜레이 후 실행: $videoId")
                                player.cueVideo(videoId, 0f)

                                player.addListener(object : AbstractYouTubePlayerListener() {
                                    override fun onCurrentSecond(p: YouTubePlayer, second: Float) {
                                        currentTimeSec = second
                                    }
                                })

                                val overlaySubtitleView = findViewById<TextView>(R.id.overlaySubtitle)
                                startSubtitleSyncTimer(overlaySubtitleView)
                            }, 500)
                        }
                    })
                }
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
                    val engLine = line.text.lineSequence().firstOrNull()?.trim() ?: ""
                    spannableBuilder.append(engLine).append("\n\n")
                }
            }

            fullSubtitleTextView.text = spannableBuilder
        }

        private fun startSubtitleSyncTimer(overlaySubtitleView: TextView) {
            subtitleTimer = Timer()
            subtitleTimer?.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    val currentMs = (currentTimeSec * 1000).toLong()
                    val currentSubtitle = subtitleList.find {
                        currentMs in (it.startTime * 1000).toLong()..(it.endTime * 1000).toLong()
                    }

                    val highlighted = currentSubtitle?.getHighlightedText(highlightWords) ?: ""

                    runOnUiThread {
                        overlaySubtitleView.text =
                            Html.fromHtml(highlighted, Html.FROM_HTML_MODE_COMPACT)
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
