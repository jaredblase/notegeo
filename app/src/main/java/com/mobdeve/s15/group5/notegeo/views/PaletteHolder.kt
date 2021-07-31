package com.mobdeve.s15.group5.notegeo.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.mobdeve.s15.group5.notegeo.R

class PaletteHolder(mContext: Context, attrs: AttributeSet?) : View(mContext, attrs) {

    val backgroundColor: Int
    val isPaletteSelected: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    private val foregroundColor: Int
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderWidth = 8F
    private val innerRadius = 40F
    private val center = innerRadius + borderWidth

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PaletteHolder)
        // get values of custom attributes from xml
        paletteColor = typedArray.getColor(R.styleable.PaletteHolder_palette_color, Color.WHITE)
        Log.d("COLOR", paletteColor.toString())
        isPaletteSelected = typedArray.getBoolean(R.styleable.PaletteHolder_is_selected, false)

        typedArray.recycle()
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
        if (isPaletteSelected.value == true) {
            paint.apply {
                strokeWidth = 2F
                color = foregroundColor
                canvas?.drawLine(41F, 48F, 49F, 60F, this)
                canvas?.drawLine(49F, 60F, 61F, 38F, this)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) =
        setMeasuredDimension(center.toInt() * 2, center.toInt() * 2)
}