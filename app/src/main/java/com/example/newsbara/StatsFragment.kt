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

class StatsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StatsAdapter
    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = (requireActivity().application as MyApp).sharedViewModel

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
                status = "SHADOWING",
                createdAt = "2025-06-01T19:21:58.410221"
            )
        )

        adapter = StatsAdapter(dummyHistoryList) { clickedItem ->
            handleContinueClick(clickedItem)
        }

        recyclerView.adapter = adapter
    }

    private fun handleContinueClick(item: HistoryItem) {
        // ViewModel에 현재 영상 정보 전달
        viewModel.setVideoData(
            id = item.videoId,
            title = item.title,
            subs = listOf() // 실제 자막이 있다면 전달
        )
        viewModel.setVideoProgress(item)

        // 다음 단계 결정
        val nextStatus = when (item.status.uppercase()) {
            "WATCHED" -> "SHADOWING"
            "SHADOWING" -> "TEST"
            "TEST" -> "DICTIONARY"
            "DICTIONARY" -> null
            else -> null
        }

        when (nextStatus) {
            "WATCHED" -> startActivity(Intent(requireContext(), VideoActivity::class.java))
            "SHADOWING" -> startActivity(Intent(requireContext(), ShadowingActivity::class.java))
            "TEST" -> startActivity(Intent(requireContext(), TestActivity::class.java))
            "DICTIONARY" -> startActivity(Intent(requireContext(), DictionaryActivity::class.java))
            null -> Toast.makeText(requireContext(), "모든 단계를 완료했어요!", Toast.LENGTH_SHORT).show()
        }
    }
}



