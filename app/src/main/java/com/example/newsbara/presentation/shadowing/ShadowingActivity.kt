package com.example.newsbara.presentation.shadowing

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsbara.adapter.ShadowingAdapter
import com.example.newsbara.data.model.shadowing.ShadowingSentence
import android.widget.Button
import androidx.activity.viewModels
import com.example.newsbara.MyApp
import com.example.newsbara.R
import com.example.newsbara.SharedViewModel
import com.example.newsbara.data.model.youtube.SubtitleLine
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class ShadowingActivity : AppCompatActivity() {

    private lateinit var highlightWords: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shadowing)

        val recyclerView = findViewById<RecyclerView>(R.id.rvSentence)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // ✅ Intent로 받은 자막 리스트 꺼내기
        @Suppress("UNCHECKED_CAST")
        val subtitleList = intent.getSerializableExtra("subtitleList") as? List<SubtitleLine> ?: emptyList()

        // ✅ 하이라이트 단어 정의
        highlightWords = listOf("accelerating", "global", "urgent")

        val filtered = subtitleList.map { it.text.lineSequence().firstOrNull()?.trim() ?: "" }
            .flatMap { sentence ->
                highlightWords
                    .filter { word -> sentence.contains(word, ignoreCase = true) }
                    .map { word -> ShadowingSentence(sentence, word) }
            }

        recyclerView.adapter = ShadowingAdapter(filtered)

        val startShadowingButton = findViewById<Button>(R.id.startShadowingButton)
        startShadowingButton.setOnClickListener {
            val bottomSheet = ShadowingBottomSheetFragment.newInstance(filtered)
            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        }
    }
}

