package com.example.newsbara.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.newsbara.R
import com.google.android.material.textfield.TextInputEditText


class OnboardingAdapter(
    private val items: MutableList<TestScoreInput>,
    private val testTypes: List<String>,
    private val onAddRow: () -> Unit
) : RecyclerView.Adapter<OnboardingAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val dd: AutoCompleteTextView = v.findViewById(R.id.dropdownType)
        val et: TextInputEditText = v.findViewById(R.id.etScore)
        val btnAdd: ImageButton = v.findViewById(R.id.btnAddField)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_onboarding, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(h: VH, pos: Int) {
        val ctx = h.itemView.context

        // 드롭다운
        h.dd.setAdapter(ArrayAdapter(ctx, android.R.layout.simple_list_item_1, testTypes))
        h.dd.setText(items[pos].type, false)
        h.dd.setOnItemClickListener { _, _, i, _ ->
            items[pos].type = testTypes[i]
        }

        // 점수
        h.et.setText(items[pos].score)
        h.et.doAfterTextChanged { items[pos].score = it?.toString().orEmpty() }

        // + 버튼: 마지막 행에서만 보이게
        h.btnAdd.visibility = if (pos == items.lastIndex) View.VISIBLE else View.GONE
        h.btnAdd.setOnClickListener { onAddRow() }
    }

    override fun getItemCount() = items.size

    fun addRow() {
        items.add(TestScoreInput())
        notifyItemInserted(items.lastIndex)
        // 이전 마지막 행의 + 버튼 숨기기 갱신
        if (items.size > 1) notifyItemChanged(items.size - 2)
    }

    fun getData(): List<TestScoreInput> = items.toList()
}

data class TestScoreInput(
    var type: String = "",
    var score: String = ""
)