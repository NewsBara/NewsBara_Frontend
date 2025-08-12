package com.example.newsbara.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsbara.R
import com.example.newsbara.adapter.OnboardingAdapter
import com.example.newsbara.presentation.login.LoginActivity
import com.example.newsbara.adapter.TestScoreInput
import dagger.hilt.android.AndroidEntryPoint
import kotlin.jvm.java

@AndroidEntryPoint
class OnboardingActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: OnboardingAdapter
    private lateinit var btnNext: Button

    // 드롭다운 항목
    private val testTypes = listOf("TOEIC", "TOEFL", "IELTS", "OPIc", "TEPS")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        recycler = findViewById(R.id.recyclerScores)
        btnNext = findViewById(R.id.btnNext)

        recycler.layoutManager = LinearLayoutManager(this)

        adapter = OnboardingAdapter(
            items = mutableListOf(TestScoreInput()),
            testTypes = testTypes,
            onAddRow = { adapter.addRow()
                recycler.scrollToPosition(adapter.itemCount - 1) }
        )
        recycler.adapter = adapter

        btnNext.setOnClickListener {
            val data = adapter.getData()

            val invalid = data.any { it.type.isBlank() || it.score.isBlank() }
            if (invalid) {
                Toast.makeText(this, "모든 항목을 입력해 주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nonNumeric = data.any { it.score.toDoubleOrNull() == null }
            if (nonNumeric) {
                Toast.makeText(this, "점수는 숫자로 입력해 주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}