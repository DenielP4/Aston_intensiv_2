package com.danielp4.drumapp

import android.graphics.Canvas
import android.graphics.Paint
import android.view.View

fun Canvas.drawDrum(
    canvas: Canvas,
    radius: Float,
    centerX: Float,
    centerY: Float,
    startAngle: Float,
    index: Int,
    sweepAngle: Float,
    paint: Paint
) {
    canvas.drawArc(
        centerX - radius,
        centerY - radius,
        centerX + radius,
        centerY + radius,
        startAngle + index * sweepAngle,
        sweepAngle,
        true,
        paint
    )
}

fun View.calculateCenter(): Pair<Float, Float> {
    val centerX = width / 2f
    val centerY = height / 2f
    return Pair(centerX, centerY)
}
