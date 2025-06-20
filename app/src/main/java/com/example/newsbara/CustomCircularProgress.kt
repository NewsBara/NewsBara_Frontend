package com.example.newsbara

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class CustomCircularProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var progress: Int = 600     // 현재 포인트
    var maxProgress: Int = 1000 // 최대 포인트

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E5DFFF")
        strokeWidth = 30f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#7A5DF8")
        strokeWidth = 30f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val size = min(width, height)
        val padding = 40f
        val radius = size / 2f - padding
        val centerX = width / 2f
        val centerY = height / 2f
        val rect = RectF(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )

        // 배경 원
        canvas.drawArc(rect, 0f, 360f, false, bgPaint)

        // 진행 원
        val sweepAngle = (progress.toFloat() / maxProgress.toFloat()) * 360f
        canvas.drawArc(rect, -90f, sweepAngle, false, progressPaint)
    }

    fun setCurrentProgress(current: Int, max: Int) {
        this.progress = current
        this.maxProgress = max
        invalidate() // 화면 다시 그리기
    }

}
