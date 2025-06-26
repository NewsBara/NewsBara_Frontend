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

        adapter = StatsAdapter { clickedItem ->
            handleContinueClick(clickedItem)
        }
        recyclerView.adapter = adapter

        // 🔥 실제로 클릭한 영상들만 관찰해서 보여주기
        viewModel.historyList.observe(viewLifecycleOwner) { historyList ->
            adapter.setItems(historyList)
        }

    }

    private fun handleContinueClick(item: HistoryItem) {
        // ViewModel에 현재 영상 정보 전달
        viewModel.setVideoData(
            id = item.videoId,
            title = item.title,
            subs = listOf()
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



