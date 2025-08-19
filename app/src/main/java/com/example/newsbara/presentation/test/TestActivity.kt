package com.example.newsbara.presentation.test

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.newsbara.MyApp
import com.example.newsbara.R
import com.example.newsbara.SharedViewModel
import com.example.newsbara.presentation.common.RealId
import com.example.newsbara.presentation.util.ResultState

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class TestActivity : AppCompatActivity() {
    private val viewModel: TestViewModel by viewModels()

    private lateinit var tvSummary: TextView
    private lateinit var tvAnswer: TextView
    private lateinit var btnInput: ImageButton
    private lateinit var btnCheck: Button
    private lateinit var backButton: ImageButton

    private var correctAnswer: String = ""
    private var explanation: String = ""

    private lateinit var realVideoId: String
    private lateinit var videoTitle: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        tvSummary = findViewById(R.id.tvSummary)
        tvAnswer = findViewById(R.id.tvAnswer)
        btnInput = findViewById(R.id.btnInput)
        btnCheck = findViewById(R.id.checkAnswersButton)

        realVideoId = intent.getStringExtra("videoId") ?: ""
        videoTitle = intent.getStringExtra("videoTitle") ?: "Untitled"
        val videoId = RealId.realVideoId
        viewModel.loadTest(videoId)

        lifecycleScope.launchWhenStarted {
            viewModel.testData.collect { state ->
                when (state) {
                    is ResultState.Success -> {
                        val test = state.data
                        correctAnswer = test.answer
                        explanation = test.explanation

                        val replaced = test.summary.replace(correctAnswer, "_______", ignoreCase = true)
                        tvSummary.text = replaced
                    }

                    is ResultState.Failure -> {
                        Toast.makeText(this@TestActivity, state.message, Toast.LENGTH_SHORT).show()
                    }

                    is ResultState.Loading -> {
                        tvSummary.text = "문제를 불러오는 중입니다..."
                    }

                    is ResultState.Error -> {
                        Toast.makeText(this@TestActivity, "에러 발생: ${state.exception.message}", Toast.LENGTH_SHORT).show()
                    }

                    ResultState.Idle -> {}
                }
            }
        }

        btnInput.setOnClickListener {
            showInputDialog()
        }

        btnCheck.setOnClickListener {
            val userAnswer = tvAnswer.text.toString().trim()
            val fragment = TestResultBottomSheetFragment.newInstance(userAnswer, correctAnswer, explanation, realVideoId, videoTitle)
            fragment.show(supportFragmentManager, "resultSheet")
        }

        backButton.setOnClickListener { finish() }
    }

    private fun showInputDialog() {
        val editText = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("정답 입력")
            .setView(editText)
            .setPositiveButton("확인") { _, _ ->
                tvAnswer.text = editText.text.toString()
            }
            .setNegativeButton("취소", null)
            .show()
    }
}