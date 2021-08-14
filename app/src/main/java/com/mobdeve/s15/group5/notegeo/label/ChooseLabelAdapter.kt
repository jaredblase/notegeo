package com.mobdeve.s15.group5.notegeo.label

import android.view.ViewGroup
import com.google.android.material.radiobutton.MaterialRadioButton
import com.mobdeve.s15.group5.notegeo.R

class ChooseLabelAdapter : LabelAdapter() {
    var lastSelectedPosition = -1

    init {
        isSelecting = true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {
        val holder = super.onCreateViewHolder(parent, viewType)

        // when radio button is selected, remove other selection
        val radioBtn = holder.itemView.findViewById<MaterialRadioButton>(R.id.label_item_rb)

        radioBtn.setOnClickListener {
            val pos = holder.bindingAdapterPosition

            if (lastSelectedPosition == pos) {
                lastSelectedPosition = -1
                getItem(pos).isChecked.set(false)
            } else {
                getItem(pos).isChecked.set(true)

                if (lastSelectedPosition != -1) {
                    getItem(lastSelectedPosition).isChecked.set(false)
                }

                lastSelectedPosition = pos
            }
        }

        return holder
    }
}