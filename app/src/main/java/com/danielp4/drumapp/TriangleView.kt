package com.danielp4.drumapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class TriangleView(
    context: Context,
    attributeSet: AttributeSet
) : View(context, attributeSet) {

    private val paintTriangle = Paint()
    private val paintTriangleBorder = Paint()
    private val pathTriangle = Path()

    init {
        paintTriangle.apply {
            color = Color.RED
            style = Paint.Style.FILL
        }
        paintTriangleBorder.apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawTriangle(canvas)
    }

    private fun drawTriangle(canvas: Canvas) {
        val centerX = width / 2f
        val centerY = height / 2f
        val size = 100f

        val x1 = centerX - size / 2
        val y1 = centerY - size / 2
        val x2 = centerX + size / 2
        val y2 = centerY - size / 2
        val x3 = centerX
        val y3 = centerY + size / 2

        pathTriangle.reset()
        pathTriangle.moveTo(x1, y1)
        pathTriangle.lineTo(x2, y2)
        pathTriangle.lineTo(x3, y3)
        pathTriangle.close()

        canvas.drawPath(pathTriangle, paintTriangle)
        canvas.drawPath(pathTriangle, paintTriangleBorder)
    }

}