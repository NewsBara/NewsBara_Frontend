package com.example.newsbara.presentation.test

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import com.example.newsbara.data.model.mypage.PointResult
import com.example.newsbara.presentation.common.RealId
import com.example.newsbara.presentation.mypage.MyPageViewModel
import com.example.newsbara.presentation.mypage.stats.StatsViewModel
import com.example.newsbara.presentation.util.ResultState
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class TestResultBottomSheetFragment : BottomSheetDialogFragment() {

    private val statsViewModel: StatsViewModel by activityViewModels()
    private val myPageViewModel: MyPageViewModel by activityViewModels()

    companion object {
        fun newInstance(userAnswer: String, correctAnswer: String, explanation: String, realVideoId: String, videoTitle: String): TestResultBottomSheetFragment {
            val fragment = TestResultBottomSheetFragment()
            val args = Bundle()
            args.putString("userAnswer", userAnswer)
            args.putString("correctAnswer", correctAnswer)
            args.putString("explanation", explanation)
            args.putString("videoId", realVideoId)
            args.putString("videoTitle", videoTitle)
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
        val realVideoId = arguments?.getString("videoId") ?: ""
        val videoTitle = arguments?.getString("videoTitle") ?: ""

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
                    val item = list.firstOrNull { it.videoId == realVideoId }
                    if (item != null) {
                        val isCorrect = userAnswer.equals(correctAnswer, ignoreCase = true)

                        lifecycleScope.launch {
                            when (val result = myPageViewModel.updatePoint(isCorrect)) {
                                is ResultState.Success<PointResult> -> {
                                    val gained = if (isCorrect) 20 else 10
                                    view?.let { rootView ->
                                        Snackbar.make(rootView, "${gained}포인트를 획득했어요!", Snackbar.LENGTH_SHORT).show()
                                    }
                                }

                                is ResultState.Failure -> {
                                    Log.e("Point", "❌ 포인트 반영 실패: ${result.message}")
                                }

                                is ResultState.Error -> {
                                    Log.e("Point", "🚨 예외 발생: ${result.exception}")
                                }
                                is ResultState.Loading -> {

                                }

                                else -> Unit
                            }
                        }
                        statsViewModel.updateHistoryStatus(item) {
                            val intent = Intent(requireContext(), DictionaryActivity::class.java).apply {
                                putExtra("videoId", realVideoId)
                                putExtra("videoTitle", videoTitle)
                            }
                            startActivity(intent)
                            dismiss()
                        }
                    } else {
                    }
                }
            }
        }

        return view
    }
}