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
import android.widget.Toast
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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBadgeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeBadgeInfo()
        myPageViewModel.fetchBadgeInfo()
    }

    private fun observeBadgeInfo() {
        lifecycleScope.launchWhenStarted {
            myPageViewModel.badgeInfoResult.collect { result ->
                when (result) {
                    is ResultState.Success -> bindBadgeInfo(result.data)
                    is ResultState.Failure -> showError("배지 정보를 불러오지 못했습니다: ${result.message}")
                    is ResultState.Error -> showError("예외 발생: ${result.exception.message}")
                    else -> Unit
                }
            }
        }
    }

    private fun bindBadgeInfo(badge: BadgeInfo) {

        binding.customCircularProgressView.setCurrentProgress(
            current = badge.currentPoints,
            max = badge.nextBadgeMinPoint
        )

        binding.tvProgressText.text = buildStyledProgress(
            current = badge.currentPoints,
            max = badge.nextBadgeMinPoint
        )

        binding.tvCurrentBadgeName.text = badge.currentBadgeName
        binding.tvNextBadgeLevel.text = badge.nextBadgeName
        binding.tvCurrentBadgeLevel.text = badge.currentBadgeName

        val badgeImageRes = getBadgeImageResource(badge.currentBadgeName)
        binding.ivBadge.setImageResource(badgeImageRes)

        val badgeImage = getBadgeImageResource(badge.currentBadgeName)
        binding.ivCurrentBadge.setImageResource(badgeImage)

        val badgeImageResNext = getBadgeImageResource(badge.nextBadgeName)
        binding.ivNextBadge.setImageResource(badgeImageResNext)


    }

    private fun buildStyledProgress(current: Int, max: Int): SpannableStringBuilder {
        return SpannableStringBuilder().apply {
            appendStyled("$current", 28, Typeface.BOLD)
            appendStyled(" / $max", 18, Typeface.NORMAL, R.color.gray_text)
            appendStyled("\npoints", 14, Typeface.NORMAL, R.color.gray_text)
        }
    }

    private fun SpannableStringBuilder.appendStyled(
        text: String,
        size: Int,
        style: Int,
        colorRes: Int? = null
    ) {
        val start = length
        append(text)
        setSpan(StyleSpan(style), start, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        setSpan(AbsoluteSizeSpan(size, true), start, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        colorRes?.let {
            setSpan(
                ForegroundColorSpan(ContextCompat.getColor(requireContext(), it)),
                start, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun getBadgeImageResource(badgeName: String): Int {
        return when (badgeName.lowercase()) {
            "level 1" -> R.drawable.ic_lv1
            "level 2" -> R.drawable.ic_lv2
            "level 3" -> R.drawable.ic_lv3_
            "level 4" -> R.drawable.ic_lv4
            "level 5" -> R.drawable.ic_lv5
            else -> R.drawable.ic_lv1
        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        Log.e("BadgeFragment", message)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
