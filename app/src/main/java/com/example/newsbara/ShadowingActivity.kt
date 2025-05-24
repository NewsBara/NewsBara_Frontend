package com.example.newsbara

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsbara.adapter.ShadowingAdapter
import com.example.newsbara.data.ShadowingSentence
import android.util.Log
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
            Log.e("ShadowingActivity", "application is not MyApp: $app")
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
    }
}
