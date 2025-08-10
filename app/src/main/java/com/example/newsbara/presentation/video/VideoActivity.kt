package com.example.newsbara.presentation.video

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.newsbara.DefinitionProvider
import com.example.newsbara.R
import com.example.newsbara.data.model.youtube.TouchableMovementMethod
import com.example.newsbara.data.model.youtube.getClickableSpannable
import com.example.newsbara.data.model.youtube.getHighlightedText
import com.example.newsbara.data.model.youtube.getStartMillis
import com.example.newsbara.domain.model.ScriptLine
import com.example.newsbara.presentation.common.ResultState
import com.example.newsbara.presentation.dictionary.DictionaryActivity
import com.example.newsbara.presentation.shadowing.ShadowingActivity
import com.example.newsbara.presentation.test.TestActivity
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask
import kotlin.collections.indexOfLast

@AndroidEntryPoint
class VideoActivity : AppCompatActivity() {

    private val viewModel: VideoViewModel by viewModels()

    private lateinit var youtubePlayerView: YouTubePlayerView
    private lateinit var fullSubtitleTextView: TextView
    private lateinit var overlaySubtitleView: TextView
    private var subtitleTimer: Timer? = null
    private var currentTimeSec: Float = 0f
    private var isTranslatedMode = false

    private lateinit var videoId: String
    private lateinit var videoTitle: String
    private var subtitleList: List<ScriptLine> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        youtubePlayerView = findViewById(R.id.youtubePlayerView)
        fullSubtitleTextView = findViewById(R.id.subtitleText)
        overlaySubtitleView = findViewById(R.id.overlaySubtitle)
        val titleTextView = findViewById<TextView>(R.id.videoTitle)
        lifecycle.addObserver(youtubePlayerView)

        videoId = intent.getStringExtra("videoId")?.trim().orEmpty()
        Log.d("ScriptFetch", "📡 요청할 videoId: [$videoId]")
        videoTitle = intent.getStringExtra("videoTitle").orEmpty()

        if (videoId.isEmpty()) {
            finish()
            return
        }

        findViewById<Button>(R.id.btnSkipToTest).setOnClickListener {
            startActivity(
                Intent(this, TestActivity::class.java)
                    .putExtra("videoId", videoId)
            )
        }

        findViewById<Button>(R.id.btnSkipToDict).setOnClickListener {
            startActivity(
                Intent(this, DictionaryActivity::class.java)
                    .putExtra("videoId", videoId)
            )
        }

        titleTextView.text = videoTitle
        fullSubtitleTextView.movementMethod = LinkMovementMethod.getInstance()

        viewModel.fetchScript(videoId)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.scriptLines.collect { result ->
                    Log.d("ScriptFetch", "📦 collect 호출됨! result = $result")
                    if (result is ResultState.Success) {
                        subtitleList = result.data
                        Log.d("ScriptFetch", "✅ 자막 데이터 수신 완료! size = ${subtitleList.size}")
                        updateFullSubtitle()
                        startSubtitleSyncTimer(subtitleList)
                    } else if (result is ResultState.Failure){
                        Log.e("ScriptFetch", "❌ 자막 불러오기 실패: ${result.message}")
                    }
                }
            }
        }

        // 자막 토글 버튼
        findViewById<Button>(R.id.toggleSubtitleModeButton).setOnClickListener {
            isTranslatedMode = !isTranslatedMode
            (it as Button).text = if (isTranslatedMode) "eng script" else "eng/kor script"
            updateFullSubtitle()
        }

        findViewById<Button>(R.id.startShadowingButton).setOnClickListener {
            val intent = Intent(this, ShadowingActivity::class.java).apply {
                putExtra("videoId", videoId)
            }
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.backButton).setOnClickListener { finish() }

        youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(player: YouTubePlayer) {
                player.cueVideo(videoId, 0f)
                player.addListener(object : AbstractYouTubePlayerListener() {
                    override fun onCurrentSecond(p: YouTubePlayer, second: Float) {
                        currentTimeSec = second
                    }
                })
            }
        })
    }

    private fun updateFullSubtitle() {
        Log.d("ScriptFetch", "🎬 [updateFullSubtitle] Called. isTranslatedMode = $isTranslatedMode")
        Log.d("ScriptFetch", "📝 subtitleList size = ${subtitleList.size}")
        val builder = SpannableStringBuilder()
        for ((index, line) in subtitleList.withIndex()) {
            val highlightWords = line.keywords.map { it.word }
            Log.d("ScriptFetch", "🔹 Line[$index] = ${line.sentence}, keywords = $highlightWords")
            val spannable = if (isTranslatedMode) {
                line.getClickableSpannable(
                    highlightWords = highlightWords,
                    context = this,
                    anchorTextView = fullSubtitleTextView,
                    onDefinitionFetch = { word -> DefinitionProvider.getDefinition(word) }
                )
            } else {
                SpannableStringBuilder(line.sentence.lineSequence().firstOrNull()?.trim().orEmpty())
            }
            builder.append(spannable).append("\n\n")
        }
        fullSubtitleTextView.text = builder
        fullSubtitleTextView.movementMethod = TouchableMovementMethod()
        fullSubtitleTextView.highlightColor = Color.TRANSPARENT
    }

    private fun startSubtitleSyncTimer(subtitles: List<ScriptLine>) {
        subtitleTimer?.cancel()
        subtitleTimer = Timer()

        var lastIndex = -1

        subtitleTimer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val currentMs = (currentTimeSec * 1000).toLong()

                val nextIndex = subtitles.indexOfLast {
                    it.getStartMillis() <= currentMs
                }

                if (nextIndex != -1 && nextIndex != lastIndex) {
                    lastIndex = nextIndex
                    val currentSubtitle = subtitles[nextIndex]
                    val highlightWords = currentSubtitle.keywords.map { it.word }
                    val highlighted = currentSubtitle.getHighlightedText(highlightWords)

                    runOnUiThread {
                        overlaySubtitleView.text = Html.fromHtml(highlighted, Html.FROM_HTML_MODE_COMPACT)
                    }
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