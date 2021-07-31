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

    private var paletteColor: Int
    private var isPaletteSelected: Boolean
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { strokeWidth = 8F }
    private val innerRadius = 40F
    private val center = innerRadius + paint.strokeWidth

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

        paint.style = Paint.Style.FILL
        paint.color = paletteColor
        canvas?.drawCircle(center, center, innerRadius, paint)

        paint.style = Paint.Style.STROKE
        paint.color = Color.parseColor("#8F8F8F")
        canvas?.drawCircle(center, center, innerRadius, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) =
        setMeasuredDimension(center.toInt() * 2, center.toInt() * 2)
}