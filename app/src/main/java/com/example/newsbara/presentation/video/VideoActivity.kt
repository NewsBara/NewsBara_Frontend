package com.example.newsbara.presentation.video

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.newsbara.DefinitionProvider
import com.example.newsbara.MyApp
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

    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        highlightWords = viewModel.highlightWords

        fullSubtitleTextView = findViewById(R.id.subtitleText)

        val toggleSubButton = findViewById<Button>(R.id.toggleSubtitleModeButton)
        toggleSubButton.setOnClickListener {
            isTranslatedMode = !isTranslatedMode
            toggleSubButton.text = if (isTranslatedMode) "eng script" else "eng/kor script"

            viewModel.subtitleList.value?.let { updateFullSubtitle(it) }
        }

        val titleTextView = findViewById<TextView>(R.id.videoTitle)
        val overlaySubtitleView = findViewById<TextView>(R.id.overlaySubtitle)
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

        viewModel.videoTitle.observe(this) { title ->
            titleTextView.text = title
        }

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
