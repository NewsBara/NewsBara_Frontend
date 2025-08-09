package com.example.newsbara.presentation.shadowing

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.newsbara.R
import com.example.newsbara.data.model.shadowing.DifferenceDto
import com.example.newsbara.domain.model.ScriptLine
import com.example.newsbara.presentation.common.ResultState
import com.example.newsbara.presentation.test.TestActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
@AndroidEntryPoint
class ShadowingBottomSheetFragment : BottomSheetDialogFragment() {

    private val viewModel: ShadowingViewModel by activityViewModels()
    private val differenceResults = mutableListOf<DifferenceDto>()

    // UI Components
    private lateinit var tvOriginSentence: TextView
    private lateinit var tvUserSentence: TextView
    private lateinit var tvScore: TextView
    private lateinit var btnMic: ImageButton
    private lateinit var btnPause: ImageButton
    private lateinit var btnContinue: Button
    private lateinit var tvState: TextView
    private lateinit var ivStateIcon: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_shadowing, container, false)

        tvOriginSentence = view.findViewById(R.id.tvOriginSentence)
        tvUserSentence = view.findViewById(R.id.tvUserSentence)
        tvScore = view.findViewById(R.id.tvScore)
        btnMic = view.findViewById(R.id.btnMic)
        btnPause = view.findViewById(R.id.btnPause)
        btnContinue = view.findViewById(R.id.btnContinue)
        tvState = view.findViewById(R.id.tvState)
        ivStateIcon = view.findViewById(R.id.ivStateIcon)

        observePronunciationResult()

        showCurrentSentence()

        btnMic.setOnClickListener {
            tvState.text = "듣는 중..."
            simulateRecordingAndEvaluate()
        }

        btnContinue.setOnClickListener {
            if (viewModel.hasNextLine()) {
                viewModel.nextLine()
                resetUI()
                showCurrentSentence()
            } else {
                startTestActivity()
            }
        }

        return view
    }

    private fun showCurrentSentence() {
        val currentLine = viewModel.getCurrentLine()
        if (currentLine != null) {
            tvOriginSentence.text = currentLine.sentence
        } else {
            Toast.makeText(requireContext(), "문장을 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    private fun resetUI() {
        tvUserSentence.text = ""
        tvScore.visibility = View.GONE
        ivStateIcon.setImageResource(R.drawable.slow_motion_video)
        tvState.text = "버튼을 누른 후 아래 내용을 말하세요."
        btnMic.visibility = View.VISIBLE
        btnContinue.visibility = View.GONE
        viewModel.resetPronunciationResult()
    }

    private fun simulateRecordingAndEvaluate() {
        try {
            val script = viewModel.getCurrentLine()?.sentence ?: return
            val dummyAudio = getWavFileFromAssets()
            viewModel.evaluatePronunciation(script, dummyAudio)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "오디오 파일 없음.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observePronunciationResult() {
        lifecycleScope.launchWhenStarted {
            viewModel.pronunciationResult.collect { result ->
                when (result) {
                    is ResultState.Success -> {
                        val data = result.data
                        val difference = data.differences.firstOrNull()

                        difference?.let {
                            tvUserSentence.text = it.pronounced
                            tvOriginSentence.text = it.expected
                            differenceResults.add(it)
                        }

                        tvScore.text = "SCORE: ${data.score}/5"
                        tvScore.visibility = View.VISIBLE
                        tvState.text = "발음 평가 완료"
                        ivStateIcon.setImageResource(R.drawable.complete)

                        btnMic.visibility = View.GONE
                        btnContinue.visibility = View.VISIBLE
                    }

                    is ResultState.Failure -> {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    is ResultState.Loading -> {
                        tvState.text = "버튼을 누른 후 아래 내용을 말하세요."
                    }

                    ResultState.Idle -> {
                    }
                }
            }
        }
    }

    private fun getWavFileFromAssets(): File {
        val assetManager = requireContext().assets
        val inputStream = assetManager.open("dummy_converted.wav")
        val tempFile = File(requireContext().cacheDir, "dummy_audio.wav")
        val outputStream = FileOutputStream(tempFile)

        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()

        return tempFile
    }

    private fun startTestActivity() {
        val intent = Intent(requireContext(), TestActivity::class.java)
        startActivity(intent)
        dismiss()
    }
}