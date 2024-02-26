package com.danielp4.drumapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.appcompat.app.AppCompatActivity
import com.danielp4.drumapp.Constants.IMAGE_URL
import com.danielp4.drumapp.Constants.TEXT
import com.danielp4.drumapp.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    var lastAngle = -1f
    var isAnimating = false
    private val sweepAngle = 360f / Constants.rainbow.keys.size
    var resultAngle = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            bStart.setOnClickListener {
                if (!isAnimating) {
                    startDrum()
                }
            }
            bReset.setOnClickListener {
                resetResult()
            }
            drumView.setOnClickListener {
                if (!isAnimating) {
                    startDrum()
                }
            }
        }
    }

    private fun resetResult() = with(binding) {
        imView.visibility = View.GONE
        finalTextView.visibility = View.GONE
    }

    private fun startDrum() = with(binding) {

        val duration = Random.nextLong(5000, 7001)
        val angle: Float = (Random.nextInt(360, 3601)).toFloat()
        val pivotX: Float = drumView.width / 2f
        val pivotY: Float = drumView.height / 2f

        val fromAngle = if(checkAngle(lastAngle)) 0f else lastAngle

        val animation = RotateAnimation(
            fromAngle,
            angle,
            pivotX,
            pivotY
        )
        lastAngle = angle % 360
        animation.duration = duration
        animation.fillAfter = true
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                isAnimating = true
            }
            override fun onAnimationRepeat(animation: Animation?) {

            }
            override fun onAnimationEnd(animation: Animation?) {
                isAnimating = false
                resultAngle = 360 - (angle % 360)
                getItem(resultAngle)
            }
        })

        drumView.startAnimation(animation)

    }

    fun checkAngle(lastAngle: Float): Boolean {
        return lastAngle == -1f
    }

    private fun getItem(angle: Float) = with(binding) {
        val index = (angle / sweepAngle).toInt()
        val result = Constants.rainbow.values.toList()[index]
        when(result) {
            IMAGE_URL -> {
                finalTextView.visibility = View.GONE
                Picasso.get().load(getString(IMAGE_URL)).into(imView)
                imView.visibility = View.VISIBLE
            }
            TEXT -> {
                imView.visibility = View.GONE
                finalTextView.visibility = View.VISIBLE
            }
        }
    }

}