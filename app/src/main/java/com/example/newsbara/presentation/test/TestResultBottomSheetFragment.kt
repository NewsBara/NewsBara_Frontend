package com.example.newsbara.presentation.test

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.newsbara.presentation.dictionary.DictionaryActivity
import com.example.newsbara.R
import com.example.newsbara.presentation.mypage.stats.StatsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
class TestResultBottomSheetFragment : BottomSheetDialogFragment() {

    private val statsViewModel: StatsViewModel by activityViewModels()

    companion object {
        fun newInstance(userAnswer: String, correctAnswer: String, explanation: String, videoId: String): TestResultBottomSheetFragment {
            val fragment = TestResultBottomSheetFragment()
            val args = Bundle()
            args.putString("userAnswer", userAnswer)
            args.putString("correctAnswer", correctAnswer)
            args.putString("explanation", explanation)
            args.putString("videoId", videoId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_test_result, container, false)

        val tvResult = view.findViewById<TextView>(R.id.tvResultText)
        val tvExplanation = view.findViewById<TextView>(R.id.tvNoteContent)
        val ivIcon = view.findViewById<ImageView>(R.id.ivResultIcon)
        val btnContinue = view.findViewById<Button>(R.id.btnContinue)

        val userAnswer = arguments?.getString("userAnswer") ?: ""
        val correctAnswer = arguments?.getString("correctAnswer") ?: ""
        val explanation = arguments?.getString("explanation") ?: ""
        val videoId = arguments?.getString("videoId") ?: ""

        val isCorrect = userAnswer.equals(correctAnswer, ignoreCase = true)

        tvResult.text = if (isCorrect) "Right" else "Wrong"
        tvExplanation.text = explanation
        if (isCorrect) {
            ivIcon.setImageResource(R.drawable.right___wrong_icon)
            tvResult.setTextColor(Color.parseColor("#5EDEC3"))
            tvResult.text = "Right"
        } else {
            ivIcon.setImageResource(R.drawable.icon_wrong)
            tvResult.setTextColor(Color.parseColor("#FF6B84"))
            tvResult.text = "Wrong"
            btnContinue.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#FF6B84"))
        }
        btnContinue.setOnClickListener {
            statsViewModel.fetchHistory()

            lifecycleScope.launchWhenStarted {
                statsViewModel.historyList.collect { list ->
                    val item = list.firstOrNull { it.videoId == videoId }
                    if (item != null) {
                        statsViewModel.updateHistoryStatus(item) {
                            // 성공 시 Dictionary 화면으로 이동
                            val intent = Intent(requireContext(), DictionaryActivity::class.java).apply {
                                putExtra("videoId", videoId)
                                putExtra("videoTitle", item.title)
                            }
                            startActivity(intent)
                            dismiss()
                        }
                    } else {
                        Toast.makeText(requireContext(), "히스토리를 찾을 수 없습니다", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        return view
    }
}