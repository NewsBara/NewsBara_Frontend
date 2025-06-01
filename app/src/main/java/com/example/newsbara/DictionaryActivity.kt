package com.example.newsbara

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsbara.adapter.DictionaryAdapter
import com.example.newsbara.data.DictionaryItem

class DictionaryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DictionaryAdapter
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var backButton: ImageButton

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

        recyclerView = findViewById(R.id.rvDictionary)
        recyclerView.layoutManager = LinearLayoutManager(this)

        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]

        val mockMeanings = mapOf(
            "accelerating" to listOf("To cause to move faster", "To speed up a process"),
            "global" to listOf("Relating to the whole world", "Comprehensive or total"),
            "urgent" to listOf("Requiring immediate action", "Pressing or critical need")
        )

        val mockData = sharedViewModel.highlightWords.map { word ->
            DictionaryItem(
                word = word,
                pronunciation = "{${word.take(3)}}",
                meanings = mockMeanings[word] ?: listOf("No definition available")
            )
        }

        adapter = DictionaryAdapter(mockData)
        recyclerView.adapter = adapter

    }
}



