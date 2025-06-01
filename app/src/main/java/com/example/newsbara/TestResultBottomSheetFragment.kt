package com.example.newsbara

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TestResultBottomSheetFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(userAnswer: String, correctAnswer: String): TestResultBottomSheetFragment {
            val fragment = TestResultBottomSheetFragment()
            val args = Bundle().apply {
                putString("userAnswer", userAnswer)
                putString("correctAnswer", correctAnswer)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_test_result, container, false)

        val tvResult = view.findViewById<TextView>(R.id.tvResultText)
        val tvExplanation = view.findViewById<TextView>(R.id.tvNoteContent)
        val ivIcon = view.findViewById<ImageView>(R.id.ivResultIcon)
        val btnContinue = view.findViewById<Button>(R.id.btnContinue)

        val userAnswer = arguments?.getString("userAnswer") ?: ""
        val correctAnswer = arguments?.getString("correctAnswer") ?: ""

        val isCorrect = userAnswer.equals(correctAnswer, ignoreCase = true)
        tvResult.text = if (isCorrect) "Right" else "Wrong"
//        ivIcon.setImageResource(
//            if (isCorrect) R.drawable.ic_check_circle else R.drawable.ic_error
//        )

        val explanation = "\"Global\" refers to something that involves the entire world. " +
                "In the context of climate change, \"global temperatures\" rising means it's a widespread problem affecting every region, " +
                "not just one country or continent."

        tvExplanation.text = explanation

        btnContinue.setOnClickListener {
            val intent = Intent(requireContext(), DictionaryActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }

        return view
    }
}
