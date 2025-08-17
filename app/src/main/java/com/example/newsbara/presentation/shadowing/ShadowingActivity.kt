package com.example.newsbara.presentation.shadowing

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsbara.adapter.ShadowingAdapter
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.newsbara.R
import com.example.newsbara.presentation.common.ResultState
import com.example.newsbara.presentation.mypage.stats.StatsViewModel
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

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.scriptLines.collect { result ->
                if (result is ResultState.Success) {
                    val scriptList = result.data
                    val filteredScriptList = scriptList.filter { it.keywords.isNotEmpty() }
                    val highlightWords = filteredScriptList.flatMap { it.keywords.map { k -> k.word } }.distinct()
                    Log.d("ShadowingActivity", "원본 스크립트 수 = ${scriptList.size}")
                    Log.d("ShadowingActivity", "키워드 있는 문장 수 = ${filteredScriptList.size}")

                    adapter = ShadowingAdapter(
                        items = filteredScriptList,
                        highlightWords = highlightWords
                    )
                    recyclerView.adapter = adapter

                    findViewById<Button>(R.id.startShadowingButton).setOnClickListener {
                        viewModel.setScriptLines(filteredScriptList)
                        val bottomSheet = ShadowingBottomSheetFragment()
                        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                    }
                }
            }
        }
    }
}