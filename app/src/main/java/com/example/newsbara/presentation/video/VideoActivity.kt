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
import android.widget.Toast
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
import com.example.newsbara.presentation.common.RealId
import com.example.newsbara.presentation.common.ResultState
import com.example.newsbara.presentation.dictionary.DictionaryActivity
import com.example.newsbara.presentation.mypage.stats.StatsViewModel
import com.example.newsbara.presentation.shadowing.ShadowingActivity
import com.example.newsbara.presentation.test.TestActivity
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask
import kotlin.collections.indexOfLast

@AndroidEntryPoint
class VideoActivity : AppCompatActivity() {

    private val viewModel: VideoViewModel by viewModels()
    private val statsViewModel: StatsViewModel by viewModels()

    private lateinit var youtubePlayerView: YouTubePlayerView
    private lateinit var fullSubtitleTextView: TextView
    private lateinit var overlaySubtitleView: TextView
    private var subtitleTimer: Timer? = null
    private var currentTimeSec: Float = 0f
    private var isTranslatedMode = false

    private lateinit var videoTitle: String
    private var subtitleList: List<ScriptLine> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        val videoId = RealId.realVideoId

        youtubePlayerView = findViewById(R.id.youtubePlayerView)
        fullSubtitleTextView = findViewById(R.id.subtitleText)
        overlaySubtitleView = findViewById(R.id.overlaySubtitle)
        val titleTextView = findViewById<TextView>(R.id.videoTitle)
        lifecycle.addObserver(youtubePlayerView)

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
            val realVideoId = intent.getStringExtra("videoId") ?: return@setOnClickListener
            val item = statsViewModel.historyList.value.firstOrNull {
                it.videoId == realVideoId
            }
            if (item != null) {
                statsViewModel.updateHistoryStatus(item) {
                    startActivity(Intent(this@VideoActivity, ShadowingActivity::class.java).apply {
                        putExtra("videoId", realVideoId)
                        putExtra("videoTitle", item.title)
                    })
                    finish()
                }
            } else {
                startActivity(Intent(this@VideoActivity, ShadowingActivity::class.java).apply {
                    putExtra("videoId", realVideoId)
                    putExtra("videoTitle", "No Title")
                })
                finish()
            }
        }

        findViewById<ImageButton>(R.id.backButton).setOnClickListener { finish() }

        youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(player: YouTubePlayer) {
                player.loadVideo(videoId, 0f)
                player.addListener(object : AbstractYouTubePlayerListener() {
                    override fun onCurrentSecond(p: YouTubePlayer, second: Float) {
                        currentTimeSec = second
                    }

                    override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                        if (error == PlayerConstants.PlayerError.VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER) {
                            Toast.makeText(this@VideoActivity, "이 영상은 앱에서 재생할 수 없습니다.", Toast.LENGTH_LONG).show()
                        } else {
                            Log.e("YouTubePlayerError", "기타 재생 오류: $error")
                        }
                    }
                })
            }
            override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                Log.e("YouTubePlayerError", "초기화 실패 또는 로딩 오류: $error")
            }
        })
    }

    private fun updateFullSubtitle() {
        Log.d("ScriptFetch", "🎬 [updateFullSubtitle] Called. isTranslatedMode = $isTranslatedMode")
        val builder = SpannableStringBuilder()
        for ((index, line) in subtitleList.withIndex()) {
            val highlightWords = line.keywords.map { it.word }
            Log.d("ScriptFetch", "🔹 Line[$index] = ${line.sentence}, keywords = $highlightWords")
            val spannable = if (isTranslatedMode) {
                line.getClickableSpannable(
                    highlightWords = highlightWords,
                    context = this,
                    anchorTextView = fullSubtitleTextView,
                    onDefinitionFetch = { clicked ->
                        val k = line.keywords.firstOrNull { it.word.equals(clicked, true) }
                        val gptKo  = k?.gptDefinitionKo?.ifBlank { "정의가 없습니다." } ?: "정의가 없습니다."
                        val bertKo = k?.bertDefinitionKo?.takeIf { it.isNotBlank() }
                        gptKo to bertKo
                    }
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