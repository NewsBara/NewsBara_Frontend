package com.example.newsbara.presentation.mypage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsbara.DictionaryActivity
import com.example.newsbara.MyApp
import com.example.newsbara.R
import com.example.newsbara.SharedViewModel
import com.example.newsbara.presentation.video.VideoActivity
import com.example.newsbara.adapter.StatsAdapter
import com.example.newsbara.data.model.history.HistoryItem
import com.example.newsbara.presentation.shadowing.ShadowingActivity
import com.example.newsbara.presentation.test.TestActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StatsAdapter

    // ✅ Hilt 기반으로 ViewModel 가져오기
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.recyclerHistory)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = StatsAdapter { clickedItem ->
            handleContinueClick(clickedItem)

        }
        recyclerView.adapter = adapter
        sharedViewModel.fetchHistory()

        // 학습 기록 변경 감지
        sharedViewModel.historyList.observe(viewLifecycleOwner) { historyList ->
            adapter.setItems(historyList)
        }
    }

        private fun handleContinueClick(item: HistoryItem) {
            sharedViewModel.setVideoData(
                id = item.videoId,
                title = item.title,
                subs = listOf()  // 실제 자막 데이터로 대체 가능
            )
            sharedViewModel.setVideoProgress(item)

            val nextStatus = when (item.status.uppercase()) {
                "WATCHED" -> "SHADOWING"
                "SHADOWING" -> "TEST"
                "TEST" -> "DICTIONARY"
                "DICTIONARY" -> null
                else -> null
            }

            when (nextStatus) {
                "WATCHED" -> startActivity(Intent(requireContext(), VideoActivity::class.java))
                "SHADOWING" -> startActivity(
                    Intent(
                        requireContext(),
                        ShadowingActivity::class.java
                    )
                )

                "TEST" -> startActivity(Intent(requireContext(), TestActivity::class.java))
                "DICTIONARY" -> startActivity(
                    Intent(
                        requireContext(),
                        DictionaryActivity::class.java
                    )
                )

                null -> Toast.makeText(requireContext(), "모든 단계를 완료했어요!", Toast.LENGTH_SHORT).show()
            }
        }
    }

