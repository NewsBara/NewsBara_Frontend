package com.example.newsbara

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.newsbara.data.BadgeInfo
import com.example.newsbara.databinding.FragmentBadgeBinding

class BadgeFragment : Fragment() {

    private var _binding: FragmentBadgeBinding? = null
    private val binding get() = _binding!!

    private val dummyBadge = BadgeInfo(
        currentBadgeName = "level 3",
        currentPoints = 600,
        nextBadgeMinPoint = 1000,
        nextBadgeName = "level 4"
    )

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
        // 프로그레스 설정
        binding.customCircularProgressView.setCurrentProgress(
            current = dummyBadge.currentPoints,
            max = dummyBadge.nextBadgeMinPoint
        )

        // 텍스트 스타일 적용
        binding.tvProgressText.text = buildStyledProgress(
            dummyBadge.currentPoints,
            dummyBadge.nextBadgeMinPoint
        )
    }

    private fun buildStyledProgress(current: Int, max: Int): SpannableStringBuilder {
        val builder = SpannableStringBuilder()

        val currentStr = current.toString()
        val maxStr = "/ $max"
        val pointsStr = "\npoints"

        // current
        builder.append(currentStr)
        builder.setSpan(StyleSpan(Typeface.BOLD), 0, currentStr.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.setSpan(AbsoluteSizeSpan(28, true), 0, currentStr.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // / max
        val startMax = builder.length
        builder.append(maxStr)
        builder.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.gray_text)), startMax, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.setSpan(AbsoluteSizeSpan(18, true), startMax, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // points
        val startPoints = builder.length
        builder.append(pointsStr)
        builder.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.gray_text)), startPoints, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.setSpan(AbsoluteSizeSpan(14, true), startPoints, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        return builder
    }
}


