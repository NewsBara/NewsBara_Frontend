package com.example.newsbara

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsbara.adapter.DictionaryAdapter
import com.example.newsbara.data.DictionaryItem
import com.example.newsbara.data.DictionaryMeaning

class DictionaryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DictionaryAdapter
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var backButton: ImageButton
    private lateinit var btnHome: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionary)

        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, TestActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }
        btnHome = findViewById(R.id.BtnHome)
        btnHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

        recyclerView = findViewById(R.id.rvDictionary)
        recyclerView.layoutManager = LinearLayoutManager(this)

        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]

        val mockMeanings = mapOf(
            "accelerating" to listOf(
                DictionaryMeaning("v.", "To cause to move faster"),
                DictionaryMeaning("v.", "To speed up a process")
            ),
            "global" to listOf(
                DictionaryMeaning("adj.", "Relating to the whole world"),
                DictionaryMeaning("adj.", "Comprehensive or total")
            ),
            "urgent" to listOf(
                DictionaryMeaning("adj.", "Requiring immediate action"),
                DictionaryMeaning("adj.", "Pressing or critical need")
            )
        )


        val mockData = sharedViewModel.highlightWords.map { word ->
            DictionaryItem(
                word = word,
                meanings = mockMeanings[word] ?: listOf(DictionaryMeaning("", "No definition available"))
            )
        }

        adapter = DictionaryAdapter(mockData)
        recyclerView.adapter = adapter

    }
}



