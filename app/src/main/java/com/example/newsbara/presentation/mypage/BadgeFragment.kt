package com.example.newsbara.presentation.mypage

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.newsbara.MyApp
import com.example.newsbara.R
import com.example.newsbara.SharedViewModel
import com.example.newsbara.data.model.mypage.BadgeInfo
import com.example.newsbara.databinding.FragmentBadgeBinding
import com.example.newsbara.presentation.util.ResultState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BadgeFragment : Fragment() {

    private var _binding: FragmentBadgeBinding? = null
    private val binding get() = _binding!!

    private val myPageViewModel: MyPageViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBadgeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        myPageViewModel.fetchBadgeInfo()

        lifecycleScope.launchWhenStarted {
            myPageViewModel.badgeInfoResult.collect { result ->
                when (result) {
                    is ResultState.Success -> {
                        val badge = result.data
                        binding.customCircularProgressView.setCurrentProgress(
                            current = badge.currentPoints,
                            max = badge.nextBadgeMinPoint
                        )

                        binding.tvProgressText.text = buildStyledProgress(
                            badge.currentPoints,
                            badge.nextBadgeMinPoint
                        )
                    }
                    is ResultState.Failure -> {
                        Log.e("BadgeFragment", "배지 정보 로딩 실패: ${result.message}")
                    }
                    is ResultState.Error -> {
                        Log.e("BadgeFragment", "배지 정보 오류: ${result.exception.message}")
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun buildStyledProgress(current: Int, max: Int): SpannableStringBuilder {
        val builder = SpannableStringBuilder()

        val currentStr = current.toString()
        val maxStr = "/ $max"
        val pointsStr = "\npoints"

        builder.append(currentStr)
        builder.setSpan(StyleSpan(Typeface.BOLD), 0, currentStr.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.setSpan(AbsoluteSizeSpan(28, true), 0, currentStr.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val startMax = builder.length
        builder.append(maxStr)
        builder.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.gray_text)),
            startMax, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        builder.setSpan(AbsoluteSizeSpan(18, true), startMax, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val startPoints = builder.length
        builder.append(pointsStr)
        builder.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.gray_text)),
            startPoints, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        builder.setSpan(AbsoluteSizeSpan(14, true), startPoints, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        return builder
    }
}
