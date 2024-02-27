package com.danielp4.drumapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.View

class DrumView(
    context: Context,
    attributeSet: AttributeSet
) : View(context, attributeSet) {

    private val paintDrum = Paint()
    private val startAngle = 0f
    private val sweepAngle = 360f / Constants.rainbow.keys.size
    private val paintBorder = Paint()
    private var radius = 0f

    init {
        paintBorder.apply {
            style = Paint.Style.STROKE
            color = Color.BLACK
            strokeWidth = 4f
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawDrum(canvas)
    }

    private fun drawDrum(canvas: Canvas) {
        val centerX = width / 2f
        val centerY = height / 2f
        Constants.rainbow.keys.forEachIndexed { index, color ->
            paintDrum.color = context.getColor(color)
            canvas.drawArc(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius,
                startAngle + index * sweepAngle,
                sweepAngle,
                true,
                paintDrum
            )

            canvas.drawArc(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius,
                startAngle + index * sweepAngle,
                sweepAngle,
                true,
                paintBorder
            )
        }


    }

    fun updateRadius(newRadius: Float) {
        radius = newRadius
        requestLayout()
        invalidate()
    }

}