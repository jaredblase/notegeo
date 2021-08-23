package com.mobdeve.s15.group5.notegeo.editor

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.content.res.AppCompatResources.getDrawable

import com.mobdeve.s15.group5.notegeo.R
import com.mobdeve.s15.group5.notegeo.dpToPx
import kotlin.math.ceil

class RemovableItemView(context: Context, attrs: AttributeSet? = null) :
    androidx.appcompat.widget.AppCompatTextView(context, attrs) {
    private lateinit var onRemoveListener: () -> Unit
    private var hasRemoveButton: Boolean = true

    init {
        maxLines = 1
        ellipsize = TextUtils.TruncateAt.END
        background = getDrawable(context, R.drawable.border)!!

        context.obtainStyledAttributes(attrs, R.styleable.RemovableItemView).apply {
            // will draw remove button unless specified otherwise
            hasRemoveButton = getBoolean(R.styleable.RemovableItemView_hasRemoveButton, true)
            recycle()
        }

        if (hasRemoveButton) {
            compoundDrawablePadding = 14

            val cross = getDrawable(context, R.drawable.ic_cross)!!.apply {
                val size = ceil(textSize * 1.2).toInt()
                setBounds(0, 0, size, size)
            }
            setCompoundDrawables(null, null, cross, null)
            setPadding(dpToPx(10), dpToPx(4), dpToPx(6), dpToPx(4))
        } else {
            setPadding(dpToPx(10), dpToPx(4), dpToPx(10), dpToPx(4))
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null && event.action == MotionEvent.ACTION_DOWN) {
            // x mark was pressed
            if (hasRemoveButton && event.rawX >= right - compoundDrawables[2].bounds.width()) {
                onRemoveListener()
                return true
            }
        }

        return super.onTouchEvent(event)
    }

    fun setOnRemoveListener(listener: () -> Unit) {
        onRemoveListener = listener
    }
}