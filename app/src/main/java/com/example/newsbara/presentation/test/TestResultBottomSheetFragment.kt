package com.example.newsbara.presentation.test

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.newsbara.presentation.dictionary.DictionaryActivity
import com.example.newsbara.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
class TestResultBottomSheetFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(userAnswer: String, correctAnswer: String, explanation: String): TestResultBottomSheetFragment {
            val fragment = TestResultBottomSheetFragment()
            val args = Bundle()
            args.putString("userAnswer", userAnswer)
            args.putString("correctAnswer", correctAnswer)
            args.putString("explanation", explanation)
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

        val isCorrect = userAnswer.equals(correctAnswer, ignoreCase = true)

        tvResult.text = if (isCorrect) "Right" else "Wrong"
        tvExplanation.text = explanation
        ivIcon.setImageResource(if (isCorrect) R.drawable.right___wrong_icon else R.drawable.right___wrong_icon)

        btnContinue.setOnClickListener {
            dismiss()
        }

        return view
    }
}