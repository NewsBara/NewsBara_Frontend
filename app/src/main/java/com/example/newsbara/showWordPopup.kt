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

fun showWordPopup(context: Context, anchor: View, word: String, definition: String, startIndex: Int) {
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

    val textView = anchor as? TextView ?: return
    textView.post {
        val layout = textView.layout ?: return@post
        val index = textView.text.toString().indexOf(word, ignoreCase = true)
        if (index == -1) return@post

        val line = layout.getLineForOffset(startIndex)
        val x = layout.getPrimaryHorizontal(startIndex).toInt()
        val y = layout.getLineTop(line)  // 또는 getLineBottom(line)

        val location = IntArray(2)
        textView.getLocationOnScreen(location)
        val screenX = location[0] + x
        val screenY = location[1] + y

        // 팝업 높이 40dp + margin 16dp
        val popupHeight = (40 * context.resources.displayMetrics.density).toInt() + 16

        popupWindow.showAtLocation(textView, Gravity.NO_GRAVITY, screenX, screenY - popupHeight)
        Log.d("PopupDebug", "word=$word, index=$index, line=$line, x=$x, y=$y")
        Log.d("PopupDebug", "lines=${layout.lineCount}, line=$line, lineTop=${layout.getLineTop(line)}, lineBottom=${layout.getLineBottom(line)}")


    }

}

