package com.example.newsbara.presentation.shadowing

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsbara.adapter.ShadowingAdapter
import com.example.newsbara.data.model.shadowing.ShadowingSentence
import android.widget.Button
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.newsbara.R
import com.example.newsbara.presentation.common.ResultState
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ShadowingActivity : AppCompatActivity() {

    private val viewModel: ShadowingViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ShadowingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shadowing)

        val videoId = intent.getStringExtra("videoId") ?: return

        recyclerView = findViewById(R.id.rvSentence)
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.fetchScriptLines(videoId)

        lifecycleScope.launchWhenStarted {
            viewModel.scriptLines.collect { result ->
                if (result is ResultState.Success) {
                    val scriptList = result.data

                    // 모든 스크립트 라인의 keyword만 모아서 highlightWords로
                    val highlightWords = scriptList.flatMap { it.keywords.map { k -> k.word } }.distinct()

                    // adapter 구성 및 연결
                    adapter = ShadowingAdapter(
                        items = scriptList,
                        highlightWords = highlightWords
                    )
                    recyclerView.adapter = adapter

                    // bottom sheet
                    findViewById<Button>(R.id.startShadowingButton).setOnClickListener {
                        val sentences = scriptList.map { it.sentence.trim() }
                            .flatMap { sentence ->
                                highlightWords.filter { word -> sentence.contains(word, ignoreCase = true) }
                                    .map { word -> ShadowingSentence(sentence, word) }
                            }
                        val bottomSheet = ShadowingBottomSheetFragment.newInstance(sentences)
                        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                    }
                }
            }
        }
    }
}