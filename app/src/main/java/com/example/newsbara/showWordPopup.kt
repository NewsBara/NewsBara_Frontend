package com.example.newsbara

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView

fun showWordPopup(
    context: Context,
    anchor: View,
    word: String,
    koDef1: String,
    koDef2: String?,
    rawX: Int,
    rawY: Int
) {
    val inflater = LayoutInflater.from(context)
    val popupView = inflater.inflate(R.layout.popup_word, null)
    val tvWord = popupView.findViewById<TextView>(R.id.wordText)
    val tvDef = popupView.findViewById<TextView>(R.id.definitionText)

    tvWord.text = word
    tvDef.text = koDef1

    val popupWindow = PopupWindow(
        popupView,
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
        true
    ).apply {
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        isOutsideTouchable = true
        elevation = 12f
    }

    popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    popupWindow.isOutsideTouchable = true

    var showingSecond = false
    popupView.setOnClickListener {
        if (koDef2 != null) {
            if (!showingSecond) {
                tvDef.text = koDef2
            } else {
                tvDef.text = koDef1
            }
            showingSecond = !showingSecond
        }
    }

    val popupHeight = (48 * context.resources.displayMetrics.density).toInt()

    popupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, rawX, rawY - popupHeight)

}