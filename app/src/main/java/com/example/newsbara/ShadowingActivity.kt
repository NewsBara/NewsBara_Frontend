package com.example.newsbara

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.newsbara.data.ShadowingSentence


class ShadowingActivity : AppCompatActivity() {

    val mockShadowingSentences = listOf(
        ShadowingSentence("Coffee has been consumed for at least 1,500 years and some say its impact is so great that it helped fuel the Enlightenment.", "fuel"),
        ShadowingSentence("The main active ingredient of coffee is caffeine, which is considered the most widely consumed psychoactive drug on the planet.", "psychoactive"),
        ShadowingSentence("Coffee comes from the fruit of the Coffea arabica plant that originated in Ethiopia.", "originated"),
        ShadowingSentence("In the 15th century, the first coffee houses began to appear across the Ottoman Empire before spreading to Europe.", "appear"),
        ShadowingSentence("By blocking adenosine receptors, caffeine generates the opposite effect of drowsiness, increasing alertness and concentration.", "drowsiness"),
        ShadowingSentence("For healthy adults, the recommended maximum limit is...", "recommended")
    )


    private val highlightColor = "#FF6B84"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shadowing)


        mockShadowingSentences.forEachIndexed { index, item ->
            val textView = TextView(this)
            textView.textSize = 16f
            textView.setTextColor(Color.BLACK)
            textView.setLineSpacing(8f, 1f)

            val numbered = "${index + 1}. ${item.sentence}"
            val spannable = SpannableString(numbered)

            val keywordIndex = numbered.indexOf(item.highlightWord, ignoreCase = true)
            if (keywordIndex != -1) {
                spannable.setSpan(
                    ForegroundColorSpan(Color.parseColor(highlightColor)),
                    keywordIndex,
                    keywordIndex + item.highlightWord.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannable.setSpan(
                    StyleSpan(Typeface.BOLD),
                    keywordIndex,
                    keywordIndex + item.highlightWord.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            textView.text = spannable
        }
    }
}
