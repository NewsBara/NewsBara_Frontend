package com.example.newsbara.presentation.shadowing

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsbara.adapter.ShadowingAdapter
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
                    val highlightWords = scriptList.flatMap { it.keywords.map { k -> k.word } }.distinct()

                    adapter = ShadowingAdapter(
                        items = scriptList,
                        highlightWords = highlightWords
                    )
                    recyclerView.adapter = adapter

                    findViewById<Button>(R.id.startShadowingButton).setOnClickListener {
                        viewModel.setScriptLines(scriptList) // ✅ ViewModel에 문장 목록 전달
                        val bottomSheet = ShadowingBottomSheetFragment() // ✅ 기존 방식으로 생성
                        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                    }
                }
            }
        }
    }
}