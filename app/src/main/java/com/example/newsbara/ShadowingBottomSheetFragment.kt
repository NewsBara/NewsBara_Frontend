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
import com.example.newsbara.data.ShadowingSentence
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ShadowingBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var tvOriginSentence: TextView
    private lateinit var tvUserSentence: TextView
    private lateinit var tvScore: TextView
    private lateinit var btnMic: ImageButton
    private lateinit var btnPause: ImageButton
    private lateinit var btnContinue: Button
    private lateinit var tvState: TextView
    private lateinit var ivStateIcon: ImageView

    private var sentenceList: List<ShadowingSentence> = emptyList()
    private var currentIndex = 0

    companion object {
        fun newInstance(sentences: List<ShadowingSentence>): ShadowingBottomSheetFragment {
            val fragment = ShadowingBottomSheetFragment()
            fragment.sentenceList = sentences
            return fragment
        }
    }

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


        showSentence(currentIndex)

        btnMic.setOnClickListener {
            tvState.text = "녹음 중..."
            // TODO: 음성 인식 시작 및 사용자 문장 비교 로직
            simulateRecognition()
        }

        btnContinue.setOnClickListener {
            currentIndex++
            if (currentIndex < sentenceList.size) {
                resetUI()
                showSentence(currentIndex)
            } else {
                startTestActivity()
            }
        }
        return view
    }

    private fun showSentence(index: Int) {
        tvOriginSentence.text = sentenceList[index].sentence
    }

    private fun simulateRecognition() {
        // 실제 음성인식 결과를 넣는 부분은 추후에 ML 모델 연결해서 교체
        val recognized = sentenceList[currentIndex].sentence
        tvUserSentence.text = recognized
        tvScore.visibility = View.VISIBLE
        tvScore.text = "SCORE: 4.5/5"
        tvState.text = "발음 평가 완료"

        ivStateIcon.setImageResource(R.drawable.complete)

        btnMic.visibility = View.GONE
        btnContinue.visibility = View.VISIBLE
    }

    private fun resetUI() {
        tvUserSentence.text = ""
        tvScore.visibility = View.GONE
        ivStateIcon.setImageResource(R.drawable.slow_motion_video)
        tvState.text = "버튼을 누른 후 아래 내용을 말하세요."
        btnMic.visibility = View.VISIBLE
        btnContinue.visibility = View.GONE
    }

    private fun startTestActivity() {
        val intent = Intent(requireContext(), TestActivity::class.java)
        startActivity(intent)
        dismiss()
    }
}
