package com.mobdeve.s15.group5.notegeo.editor

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import com.mobdeve.s15.group5.notegeo.R
import com.mobdeve.s15.group5.notegeo.dpToPx
import kotlin.math.ceil

class RemovableItemView(context: Context, attrs: AttributeSet? = null) :
    androidx.appcompat.widget.AppCompatTextView(context, attrs) {
    private lateinit var onRemoveListener: () -> Unit

    init {
        setPadding(dpToPx(10), dpToPx(4), dpToPx(6), dpToPx(4))
        maxLines = 1
        background = getDrawable(context, R.drawable.border)!!

        compoundDrawablePadding = 14
        val cross = getDrawable(context, R.drawable.ic_cross)!!.apply {
            val size = ceil(textSize * 1.2).toInt()
            setBounds(0, 0, size, size)
        }

        setCompoundDrawables(null, null, cross, null)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null && event.action == MotionEvent.ACTION_DOWN) {

            // x mark was pressed
            if (event.rawX >= right - compoundDrawables[2].bounds.width()) {
                onRemoveListener()
            } else {
                performClick()
            }

            return true
        }

        return super.onTouchEvent(event)
    }

    fun setOnRemoveListener(listener: () -> Unit) {
        onRemoveListener = listener
    }
}