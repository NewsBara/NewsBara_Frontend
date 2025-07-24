package com.example.newsbara

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsbara.adapter.DictionaryAdapter
import com.example.newsbara.data.DictionaryItem
import com.example.newsbara.data.DictionaryMeaning
import com.example.newsbara.presentation.home.HomeActivity
import com.example.newsbara.presentation.test.TestActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DictionaryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DictionaryAdapter
    private lateinit var backButton: ImageButton
    private lateinit var btnHome: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionary)

        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            startActivity(Intent(this, TestActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            })
            finish()
        }

        btnHome = findViewById(R.id.BtnHome)
        btnHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            })
            finish()
        }

        recyclerView = findViewById(R.id.rvDictionary)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // highlightWords 가져오기
        val highlightWords = intent.getStringArrayListExtra("highlightWords") ?: listOf()

        // 모의 데이터 생성
        val mockMeanings = mapOf(
            "accelerating" to listOf(
                DictionaryMeaning("v.", "더 빠르게 움직이게 하다"),
                DictionaryMeaning("v.", "과정을 가속하다")
            ),
            "global" to listOf(
                DictionaryMeaning("adj.", "전 세계적인"),
                DictionaryMeaning("adj.", "포괄적이거나 전체적인")
            ),
            "urgent" to listOf(
                DictionaryMeaning("adj.", "즉각적인 조치를 요구하는"),
                DictionaryMeaning("adj.", "긴급하거나 중대한 필요가 있는")
            )
        )

        val mockData = highlightWords.map { word ->
            DictionaryItem(
                word = word,
                meanings = mockMeanings[word] ?: listOf(DictionaryMeaning("", "No definition available"))
            )
        }

        adapter = DictionaryAdapter(mockData)
        recyclerView.adapter = adapter
    }
}




