package com.example.newsbara.presentation.mypage.stats

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsbara.DictionaryActivity
import com.example.newsbara.R
import com.example.newsbara.SharedViewModel
import com.example.newsbara.adapter.StatsAdapter
import com.example.newsbara.data.model.history.HistoryItem
import com.example.newsbara.presentation.shadowing.ShadowingActivity
import com.example.newsbara.presentation.test.TestActivity
import com.example.newsbara.presentation.video.VideoActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StatsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StatsAdapter

    private val statsViewModel: StatsViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_stats, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.recyclerHistory)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = StatsAdapter { clickedItem ->
            handleContinueClick(clickedItem)
        }
        recyclerView.adapter = adapter

        statsViewModel.fetchHistory()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                statsViewModel.historyList.collect { history ->
                    adapter.setItems(history)
                }
            }
        }
    }

    private fun handleContinueClick(item: HistoryItem) {
        statsViewModel.updateHistoryStatus(item) { updatedItem ->
            if (updatedItem == null) {
                Toast.makeText(requireContext(), "모든 단계를 완료했어요!", Toast.LENGTH_SHORT).show()
                return@updateHistoryStatus
            }

            sharedViewModel.setVideoData(
                id = updatedItem.videoId,
                title = updatedItem.title,
                subs = listOf() // 자막 연결 예정
            )
            sharedViewModel.setVideoProgress(updatedItem)

            val intent = when (updatedItem.status.uppercase()) {
                "WATCHED" -> Intent(requireContext(), VideoActivity::class.java)
                "SHADOWING" -> Intent(requireContext(), ShadowingActivity::class.java)
                "TEST" -> Intent(requireContext(), TestActivity::class.java)
                "DICTIONARY" -> Intent(requireContext(), DictionaryActivity::class.java)
                else -> null
            }

            intent?.let { startActivity(it) }
        }
    }
}
