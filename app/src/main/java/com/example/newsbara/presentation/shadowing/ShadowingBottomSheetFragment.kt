package com.example.newsbara.presentation.shadowing

import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.flow.collectLatest
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.newsbara.R
import com.example.newsbara.data.model.shadowing.DifferenceDto
import com.example.newsbara.presentation.common.ResultState
import com.example.newsbara.presentation.mypage.stats.StatsViewModel
import com.example.newsbara.presentation.test.TestActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.newsbara.data.model.history.HistoryItem
import com.example.newsbara.presentation.common.RealId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream

@AndroidEntryPoint
class ShadowingBottomSheetFragment : BottomSheetDialogFragment() {

    private val viewModel: ShadowingViewModel by activityViewModels()
    private val statsViewModel: StatsViewModel by activityViewModels()
    private val differenceResults = mutableListOf<DifferenceDto>()

    private lateinit var tvOriginSentence: TextView
    private lateinit var tvUserSentence: TextView
    private lateinit var tvScore: TextView
    private lateinit var btnMic: ImageButton
    private lateinit var btnPause: ImageButton
    private lateinit var btnContinue: Button
    private lateinit var tvState: TextView
    private lateinit var ivStateIcon: ImageView
    private lateinit var audioRecord: AudioRecord
    private lateinit var recordedFile: File
    private lateinit var realVideoId: String
    private var isRecording = false

    private val sampleRate = 16000
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_shadowing, container, false)
        realVideoId = requireActivity().intent.getStringExtra("videoId") ?: ""
        val videoId = RealId.realVideoId

        tvOriginSentence = view.findViewById(R.id.tvOriginSentence)
        tvUserSentence = view.findViewById(R.id.tvUserSentence)
        tvScore = view.findViewById(R.id.tvScore)
        btnMic = view.findViewById(R.id.btnMic)
        btnPause = view.findViewById(R.id.btnPause)
        btnContinue = view.findViewById(R.id.btnContinue)
        tvState = view.findViewById(R.id.tvState)
        ivStateIcon = view.findViewById(R.id.ivStateIcon)

        checkAndRequestPermission()
        observePronunciationResult()
        showCurrentSentence()

        btnMic.setOnClickListener {
            if (!isRecording) {
                // 녹음 시작
                resetUI()
                tvState.text = "녹음 중..."
                startRecording()

                isRecording = true
                btnMic.setImageResource(R.drawable.ic_pause)
            } else {
                // 녹음 중지 및 평가 시작
                stopRecordingAndEvaluate()

                isRecording = false
                btnMic.setImageResource(R.drawable.mike)
            }
        }

        btnContinue.setOnClickListener {
            viewModel.resetPronunciationResult()
            if (viewModel.hasNextLine()) {
                viewModel.nextLine()
                resetUI()
                showCurrentSentence()
            } else {
                startTestActivity(realVideoId)
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isRecording = false

        if (::audioRecord.isInitialized) {
            try {
                if (audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                    audioRecord.stop()
                }
                audioRecord.release()
            } catch (e: Exception) {
                Log.e("AudioRecord", "release 중 오류: ${e.message}", e)
            }
        }
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


    private fun stopRecordingAndEvaluate() {
        isRecording = false
        tvState.post {
            tvState.text = "평가 중..."
        }
    }

    private fun writeWavFile(pcmData: ByteArray, outputFile: File) {
        val wavFile = FileOutputStream(outputFile)
        val totalAudioLen = pcmData.size.toLong()
        val totalDataLen = totalAudioLen + 36
        val byteRate = 16 * sampleRate * 1 / 8

        val header = ByteArray(44)

        header[0] = 'R'.code.toByte()
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        writeInt(header, 4, totalDataLen.toInt())
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte()
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        writeInt(header, 16, 16)
        writeShort(header, 20, 1.toShort())
        writeShort(header, 22, 1.toShort())
        writeInt(header, 24, sampleRate)
        writeInt(header, 28, byteRate)
        writeShort(header, 32, 2.toShort())
        writeShort(header, 34, 16.toShort())
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        writeInt(header, 40, totalAudioLen.toInt())

        wavFile.write(header, 0, 44)
        wavFile.write(pcmData)
        wavFile.close()
    }

    private fun writeInt(header: ByteArray, offset: Int, value: Int) {
        header[offset] = (value and 0xff).toByte()
        header[offset + 1] = (value shr 8 and 0xff).toByte()
        header[offset + 2] = (value shr 16 and 0xff).toByte()
        header[offset + 3] = (value shr 24 and 0xff).toByte()
    }

    private fun writeShort(header: ByteArray, offset: Int, value: Short) {
        header[offset] = (value.toInt() and 0xff).toByte()
        header[offset + 1] = (value.toInt() shr 8 and 0xff).toByte()
    }

    private fun observePronunciationResult() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.pronunciationResult.collectLatest { result ->
                    Log.d("ShadowingFragment", "결과 상태 변경됨: $result")
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
                            Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT)
                                .show()
                        }

                        is ResultState.Loading -> {

                        }

                        is ResultState.Idle -> {
                        }
                    }
                }
            }
        }
    }


    private fun checkAndRequestPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.RECORD_AUDIO),
                1001
            )
        }
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private fun startRecording() {
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )
        val buffer = ByteArray(bufferSize)
        val pcmOutputStream = ByteArrayOutputStream()
        isRecording = true

        audioRecord.startRecording()

        Thread {
            while (isRecording) {
                val read = audioRecord.read(buffer, 0, buffer.size)
                if (read > 0) {
                    pcmOutputStream.write(buffer, 0, read)
                }
            }
            audioRecord.stop()
            audioRecord.release()

            recordedFile = File(requireContext().cacheDir, "recorded_audio.wav")
            writeWavFile(pcmOutputStream.toByteArray(), recordedFile)

            val script = viewModel.getCurrentLine()?.sentence ?: return@Thread

            requireActivity().runOnUiThread {
                viewModel.evaluatePronunciation(script, recordedFile)
            }

        }.start()
    }


    private fun startTestActivity(realVideoId: String) {
        val item = statsViewModel.historyList.value.firstOrNull {
            it.videoId == realVideoId
        }

        if (item == null) {
            Toast.makeText(requireContext(), "히스토리 항목을 찾을 수 없습니다", Toast.LENGTH_SHORT).show()
            return
        }

        statsViewModel.updateHistoryStatus(item) {
            val intent = Intent(requireContext(), TestActivity::class.java).apply {
                putExtra("videoId", realVideoId)
                putExtra("videoTitle", item.title)
            }
            startActivity(intent)
            dismiss()
        }
    }
}