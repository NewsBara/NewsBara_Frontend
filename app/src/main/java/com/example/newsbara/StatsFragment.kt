package com.example.newsbara

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsbara.adapter.StatsAdapter
import com.example.newsbara.data.HistoryItem
import com.example.newsbara.data.Step
import com.example.newsbara.data.VideoItem
import com.example.newsbara.data.VideoProgress

class StatsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StatsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.recyclerHistory)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val dummyHistoryList = listOf(
            HistoryItem(
                id = 1,
                videoId = "3hwEpIr-g5w",
                title = "Should we eat less rice? 🌍 6 Minute English",
                thumbnail = "https://i.ytimg.com/vi/3hwEpIr-g5w/mqdefault.jpg",
                channel = "BBC Learning English",
                length = "370",
                category = "기타",
                status = "WATCHED",
                createdAt = "2025-05-25T01:56:52.807818"
            ),
            HistoryItem(
                id = 2,
                videoId = "253dg4s",
                title = "ttttt",
                thumbnail = "https://i.ytimg.com/vi/253dg4s/mqdefault.jpg",
                channel = "bbc",
                length = "00:03:33",
                category = "Music",
                status = "WATCHED",
                createdAt = "2025-06-01T19:21:58.410221"
            )
        )

        adapter = StatsAdapter(dummyHistoryList) { clickedItem ->
            handleContinueClick(clickedItem)
        }
        recyclerView.adapter = adapter
    }
    private fun handleContinueClick(historyItem: HistoryItem) {
        val viewModel = (requireActivity().application as MyApp).sharedViewModel

        // 임시 VideoProgress로 변환 (나중에 백엔드에서 단계 정보 제공 시 수정)
        val dummyProgress = VideoProgress(
            videoId = historyItem.videoId,
            title = historyItem.title,
            thumbnailUrl = historyItem.thumbnail,
            completedStep = Step.VIDEO  // 또는 Step.TEST 등으로 변경 가능
        )

        viewModel.setVideoData(dummyProgress.videoId, dummyProgress.title, listOf())
        viewModel.setVideoProgress(dummyProgress)

        val nextStep = when (dummyProgress.completedStep) {
            Step.VIDEO -> Step.SHADOWING
            Step.SHADOWING -> Step.TEST
            Step.TEST -> Step.DICTIONARY
            Step.DICTIONARY -> null
        }

        when (nextStep) {
            Step.SHADOWING -> startActivity(Intent(requireContext(), ShadowingActivity::class.java))
            Step.TEST -> startActivity(Intent(requireContext(), TestActivity::class.java))
            Step.DICTIONARY -> startActivity(Intent(requireContext(), DictionaryActivity::class.java))
            Step.VIDEO -> startActivity(Intent(requireContext(), VideoActivity::class.java))
            null -> Toast.makeText(requireContext(), "모든 단계를 완료했어요!", Toast.LENGTH_SHORT).show()

        }
    }
}


