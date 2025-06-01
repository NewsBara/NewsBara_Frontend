package com.example.newsbara

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.newsbara.data.SubtitleLine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TestActivity : AppCompatActivity() {

    private lateinit var tvSummary: TextView
    private lateinit var tvAnswer: TextView
    private lateinit var btnInput: ImageButton
    private lateinit var btnCheck: Button
    private lateinit var viewModel: SharedViewModel

    private val correctAnswer = "global"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val app = application
        if (app is MyApp) {
            viewModel = app.sharedViewModel
        }

        tvSummary = findViewById(R.id.tvSummary)
        tvAnswer = findViewById(R.id.tvAnswer)
        btnInput = findViewById(R.id.btnInput)
        btnCheck = findViewById(R.id.checkAnswersButton)

        lifecycleScope.launch {
            val summary = withContext(Dispatchers.Default) {
                getGeneratedSummary(viewModel.subtitleList.value ?: emptyList())
            }
            tvSummary.text = summary
        }

        btnInput.setOnClickListener {
            showInputDialog()
        }

        btnCheck.setOnClickListener {
            val userAnswer = tvAnswer.text.toString().trim()
            val fragment = TestResultBottomSheetFragment.newInstance(userAnswer, correctAnswer)
            fragment.show(supportFragmentManager, "resultSheet")
        }
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

    private fun getGeneratedSummary(subtitles: List<SubtitleLine>): String {
        val lines = subtitles.map { it.text.lineSequence().firstOrNull()?.trim() ?: "" }
        val usedLines = lines.take(3).toMutableList()
        val replaced = usedLines.map {
            if (it.contains(correctAnswer, ignoreCase = true))
                it.replace(correctAnswer, "_______", ignoreCase = true)
            else it
        }
        return replaced.joinToString(" ")
    }
}