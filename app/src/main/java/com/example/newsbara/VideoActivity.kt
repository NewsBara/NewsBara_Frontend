package com.example.newsbara

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.newsbara.data.SubtitleLine
import com.example.newsbara.data.getClickableSpannable
import com.example.newsbara.data.getHighlightedText
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import java.util.Timer
import java.util.TimerTask

class VideoActivity : AppCompatActivity() {

    private lateinit var youtubePlayerView: YouTubePlayerView
    private var subtitleTimer: Timer? = null
    private var currentTimeSec: Float = 0f
    private var isTranslatedMode = false
    private lateinit var fullSubtitleTextView: TextView
    private lateinit var highlightWords: List<String>

    lateinit var viewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        // 여기서 바로 캐스팅
        val app = application
        if (app is MyApp) {
            viewModel = app.sharedViewModel
        } else {
            Log.e("VideoActivity", "❌ application is not MyApp: $app")
            finish()
            return
        }
    //    viewModel = (application as MyApp).sharedViewModel
        highlightWords = viewModel.highlightWords


        fullSubtitleTextView = findViewById(R.id.subtitleText)

        val toggleSubButton = findViewById<Button>(R.id.toggleSubtitleModeButton)
        toggleSubButton.setOnClickListener {
            isTranslatedMode = !isTranslatedMode
            toggleSubButton.text = if (isTranslatedMode) "한영 자막 보기 완료" else "script"

            viewModel.subtitleList.value?.let { updateFullSubtitle(it) }
        }

        val titleTextView = findViewById<TextView>(R.id.videoTitle)
        val overlaySubtitleView = findViewById<TextView>(R.id.overlaySubtitle)
        val fullSubtitleTextView = findViewById<TextView>(R.id.subtitleText)
        fullSubtitleTextView.movementMethod = LinkMovementMethod.getInstance()

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener { finish() }

        val startShadowingButton = findViewById<Button>(R.id.startShadowingButton)
        startShadowingButton.setOnClickListener {
            val intent = Intent(this@VideoActivity, ShadowingActivity::class.java)
            startActivity(intent)
        }

        youtubePlayerView = findViewById(R.id.youtubePlayerView)
        lifecycle.addObserver(youtubePlayerView)

        viewModel.videoTitle.observe(this) { titleTextView.text = it }

        viewModel.subtitleList.observe(this) { subtitles ->
            updateFullSubtitle(subtitles)
        }

        viewModel.videoId.observe(this) { videoId ->
            youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(player: YouTubePlayer) {
                    player.cueVideo(videoId, 0f)

                    player.addListener(object : AbstractYouTubePlayerListener() {
                        override fun onCurrentSecond(p: YouTubePlayer, second: Float) {
                            currentTimeSec = second
                        }
                    })
                    startSubtitleSyncTimer(overlaySubtitleView)
                }
            })
        }
    }

    private fun updateFullSubtitle(subtitles: List<SubtitleLine>) {
        val spannableBuilder = SpannableStringBuilder()

        for (line in subtitles) {
            if (isTranslatedMode) {
                val spannable = line.getClickableSpannable(highlightWords, this) { word, definition ->
                    showWordPopup(this, fullSubtitleTextView, word, definition)
                }
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
                val subtitleList = viewModel.subtitleList.value ?: return
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



