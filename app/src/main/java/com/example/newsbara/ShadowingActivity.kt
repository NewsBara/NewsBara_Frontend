package com.example.newsbara

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsbara.adapter.ShadowingAdapter
import com.example.newsbara.data.ShadowingSentence
import android.util.Log
import android.widget.Button

class ShadowingActivity : AppCompatActivity() {

    private lateinit var viewModel: SharedViewModel
    private lateinit var highlightWords: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shadowing)

        val app = application
        if (app is MyApp) {
            viewModel = app.sharedViewModel
            highlightWords = viewModel.highlightWords
        } else {
            finish()
            return
        }

        val recyclerView = findViewById<RecyclerView>(R.id.rvSentence)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val subtitleList = viewModel.subtitleList.value ?: emptyList()

        val filtered = subtitleList.map { it.text.lineSequence().firstOrNull()?.trim() ?: "" }
            .flatMap { sentence ->
                highlightWords
                    .filter { word -> sentence.contains(word, ignoreCase = true) }
                    .map { word -> ShadowingSentence(sentence, word) }
            }

        recyclerView.adapter = ShadowingAdapter(filtered)

        val startShadowingButton = findViewById<Button>(R.id.startShadowingButton)

        startShadowingButton.setOnClickListener {
            val bottomSheet = ShadowingBottomSheetFragment.newInstance(filtered) // filtered는 문장 리스트
            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        }

    }
}
