package com.example.newsbara.presentation.dictionary

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsbara.R
import com.example.newsbara.adapter.DictionaryAdapter
import com.example.newsbara.presentation.common.ResultState
import com.example.newsbara.presentation.home.HomeActivity
import com.example.newsbara.presentation.test.TestActivity
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class DictionaryActivity : AppCompatActivity() {

    private val viewModel: DictionaryViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DictionaryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionary)

        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            startActivity(Intent(this, TestActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            })
            finish()
        }
        findViewById<Button>(R.id.BtnHome).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            })
            finish()
        }

        recyclerView = findViewById(R.id.rvDictionary)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = DictionaryAdapter(emptyList())
        recyclerView.adapter = adapter

        val videoId = intent.getStringExtra("videoId")
        if (videoId.isNullOrBlank()) {
            Toast.makeText(this, "videoId 없음", Toast.LENGTH_SHORT).show()
            finish(); return
        }

        viewModel.load(videoId)

        lifecycleScope.launchWhenStarted {
            viewModel.entries.collect { state ->
                when (state) {
                    is ResultState.Success -> adapter.submit(state.data)
                    is ResultState.Failure -> Toast.makeText(this@DictionaryActivity, state.message, Toast.LENGTH_SHORT).show()
                    is ResultState.Loading, ResultState.Idle -> Unit
                }
            }
        }
    }
}