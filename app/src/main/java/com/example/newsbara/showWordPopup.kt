package com.example.newsbara

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView

fun showWordPopup(context: Context, anchor: View, word: String, definition: String) {
    val inflater = LayoutInflater.from(context)
    val popupView = inflater.inflate(R.layout.popup_word, null)
    val wordText = popupView.findViewById<TextView>(R.id.wordText)
    val defText = popupView.findViewById<TextView>(R.id.definitionText)

    wordText.text = word
    defText.text = definition

    val popupWindow = PopupWindow(
        popupView,
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
        true
    )
    popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    popupWindow.isOutsideTouchable = true

    // 더 안정적인 위치 방식
    popupWindow.showAsDropDown(anchor, 0, -anchor.height - 30)
}
