package com.example.newsbara

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.newsbara.data.BadgeInfo

class BadgeFragment : Fragment() {

    private lateinit var customProgressView: CustomCircularProgressView
    private lateinit var tvProgressText: TextView

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
        return inflater.inflate(R.layout.fragment_badge, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        customProgressView = view.findViewById(R.id.customCircularProgressView)
        tvProgressText = view.findViewById(R.id.tvProgressText)

        // 🔄 프로그레스 수치 설정
        customProgressView.setCurrentProgress(
            current = dummyBadge.currentPoints,
            max = dummyBadge.nextBadgeMinPoint
        )

        // ✅ 텍스트 스타일 설정
        tvProgressText.text = buildStyledProgress(dummyBadge.currentPoints, dummyBadge.nextBadgeMinPoint)
    }

    private fun buildStyledProgress(current: Int, max: Int): SpannableString {
        val progressText = "$current / $max\npoints"
        val spannable = SpannableString(progressText)

        // "600"
        spannable.setSpan(StyleSpan(Typeface.BOLD), 0, current.toString().length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(AbsoluteSizeSpan(28, true), 0, current.toString().length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        // "/ 1000"
        val slashIndex = progressText.indexOf("/")
        val endOfMax = slashIndex + 2 + max.toString().length
        spannable.setSpan(ForegroundColorSpan(Color.parseColor("#999999")), slashIndex, endOfMax, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(AbsoluteSizeSpan(18, true), slashIndex, endOfMax, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        // "points"
        val pointsIndex = progressText.indexOf("points")
        spannable.setSpan(ForegroundColorSpan(Color.parseColor("#999999")), pointsIndex, progressText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(AbsoluteSizeSpan(14, true), pointsIndex, progressText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        return spannable
    }
}

