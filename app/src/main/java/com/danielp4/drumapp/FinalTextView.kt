package com.danielp4.drumapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat

class FinalTextView(
    context: Context,
    attributeSet: AttributeSet
) : View(context, attributeSet){

    val paintResultText = Paint()
    private var finalText: String = ""
    private var finalTextColor: Int = Color.BLACK
    private var finalTextSize: Float = 40f
    private var finalTypeface: Typeface? = null
    init {
        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.FinalTextView,
            0, 0
        ).apply {
            try {
                finalText = getString(R.styleable.FinalTextView_finalText) ?: ""
                finalTextColor = getColor(R.styleable.FinalTextView_finalTextColor, Color.BLACK)
                finalTextSize = getDimension(R.styleable.FinalTextView_finalTextSize, 40f)
                val fontFamilyResId = getResourceId(R.styleable.FinalTextView_finalFontFamily, 0)
                if (fontFamilyResId != 0) {
                    finalTypeface = ResourcesCompat.getFont(context, fontFamilyResId)
                }
            } finally {
                recycle()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawText(canvas)
    }

    private fun drawText(canvas: Canvas) {
        paintResultText.apply {
            color = finalTextColor
            textSize = finalTextSize
            typeface?.let { typeface = it }
        }
        paintResultText.textSize = finalTextSize
        paintResultText.typeface = finalTypeface

        val textWidth = paintResultText.measureText(finalText)
        val x = (width - textWidth) / 2f
        val y = height / 2f + paintResultText.textSize / 2f
        canvas.drawText(finalText, x, y, paintResultText)
    }


}