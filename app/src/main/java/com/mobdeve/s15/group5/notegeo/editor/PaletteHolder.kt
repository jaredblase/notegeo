package com.mobdeve.s15.group5.notegeo.editor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.mobdeve.s15.group5.notegeo.R
import com.mobdeve.s15.group5.notegeo.dpToPx

class PaletteHolder(mContext: Context, attrs: AttributeSet?) : View(mContext, attrs) {

    val backgroundColor: Int
    var isPaletteSelected = true
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    private val foregroundColor: Int
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderWidth = dpToPx(3).toFloat()
    private val innerRadius = dpToPx(15).toFloat()
    private val center = innerRadius + borderWidth

    init {
        context.obtainStyledAttributes(attrs, R.styleable.PaletteHolder).apply {
            // get values of custom attributes from xml
            backgroundColor = getColor(R.styleable.PaletteHolder_note_background_color, Color.WHITE)
            foregroundColor = getColor(R.styleable.PaletteHolder_note_foreground_color, Color.BLACK)
            recycle()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // inner circle
        paint.apply {
            style = Paint.Style.FILL
            color = backgroundColor
            canvas?.drawCircle(center, center, innerRadius, this)
        }

        // border
        paint.apply {
            style = Paint.Style.STROKE
            strokeWidth = borderWidth
            color = Color.parseColor("#8F8F8F")
            canvas?.drawCircle(center, center, innerRadius, this)
        }

        // draw checkmark
        if (isPaletteSelected) {
            paint.apply {
                strokeWidth = 2F
                color = foregroundColor
                canvas?.drawLine(39F, 48F, 47F, 60F, this)
                canvas?.drawLine(47F, 60F, 59F, 38F, this)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) =
        setMeasuredDimension(center.toInt() * 2, center.toInt() * 2)
}