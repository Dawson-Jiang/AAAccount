package com.dawson.aaaccount.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * Created by Dawson on 2017/7/11.
 */

class IconView : View {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onDraw(canvas: Canvas) {
        val w = width
        val h = height
        val paint = Paint()
        paint.color = Color.parseColor("#FD9742")
        paint.strokeWidth = 6f
        paint.isAntiAlias = true

        canvas.drawLine((w / 2).toFloat(), 0f, 0f, h.toFloat(), paint)
        canvas.drawLine((w / 2).toFloat(), 0f, w.toFloat(), h.toFloat(), paint)
        canvas.drawLine((w / 4).toFloat(), (h / 2).toFloat(), (w / 2).toFloat(), h.toFloat(), paint)
        canvas.drawLine((w * 3 / 4).toFloat(), (h / 2).toFloat(), (w / 2).toFloat(), h.toFloat(), paint)

        canvas.drawLine((w * 3 / 8).toFloat(), (h / 4).toFloat(), (w * 5 / 8).toFloat(), (h / 4).toFloat(), paint)
        canvas.drawLine((w * 1 / 8).toFloat(), (h * 3 / 4).toFloat(), (w * 3 / 8).toFloat(), (h * 3 / 4).toFloat(), paint)
        canvas.drawLine((w * 5 / 8).toFloat(), (h * 3 / 4).toFloat(), (w * 7 / 8).toFloat(), (h * 3 / 4).toFloat(), paint)
    }
}
