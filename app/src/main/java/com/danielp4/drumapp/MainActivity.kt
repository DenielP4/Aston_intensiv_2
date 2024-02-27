package com.danielp4.drumapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.danielp4.drumapp.Constants.IMAGE_URL
import com.danielp4.drumapp.Constants.TEXT
import com.danielp4.drumapp.databinding.ActivityMainBinding
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import java.util.function.DoublePredicate
import kotlin.math.abs
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    var lastAngle = -1f
    var isAnimating = false
    private val sweepAngle = 360f / Constants.rainbow.keys.size
    var resultAngle = 0f

    private val viewModel: DrumViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupSeekBarListener()
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

        if (binding.drumView.rotation != 0f) {
            binding.drumView.rotation = 0f
        }

        val fromAngle =
            if (checkAngle(viewModel.lastAngle.value)) 0f else viewModel.lastAngle.value!!
        val animation = RotateAnimation(
            fromAngle,
            angle,
            pivotX,
            pivotY
        )
        viewModel.lastAngle.value = angle % 360
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

    fun checkAngle(lastAngle: Float?): Boolean {
        return lastAngle == -1f || lastAngle == null
    }

    private fun getItem(angle: Float) = with(binding) {
        val index = (abs(angle - 90) / sweepAngle).toInt()
        viewModel.result.value = Constants.rainbow.values.toList()[index]
        when (viewModel.result.value) {
            IMAGE_URL -> {
                finalTextView.visibility = View.GONE
                Picasso.get().invalidate(getString(IMAGE_URL))
                Picasso.get()
                    .load(getString(IMAGE_URL))
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(imView)
                imView.visibility = View.VISIBLE
                Log.d("MyLog", "IMAGE_URL")
            }

            TEXT -> {
                imView.visibility = View.GONE
                finalTextView.visibility = View.VISIBLE
                Log.d("MyLog", "TEXT")
            }

            else -> {}
        }
    }

    private fun calculateNewRadius(progress: Int): Float {
        val min = 0
        val max = 100
        val minRadius = 150f
        val maxRadius = 400f
        val ratio = (progress - min) / (max - min).toFloat()
        return minRadius + ratio * (maxRadius - minRadius)
    }

    private fun updateDrumSize(progress: Int) {
        val newRadius = calculateNewRadius(progress)
        Log.d("MyLog", "$newRadius")
        binding.drumView.updateRadius(newRadius)
        binding.drumView.invalidate()
    }

    private fun setupSeekBarListener() {
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateDrumSize(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                viewModel.progressRadiusDrum.value = seekBar?.progress
            }
        })
    }
    override fun onResume() {
        super.onResume()
        if (!checkAngle(viewModel.lastAngle.value)) {
            val angle = 360 - (viewModel.lastAngle.value!! % 360)
            getItem(angle)
            binding.drumView.rotation = viewModel.lastAngle.value!!
        }
        if (viewModel.progressRadiusDrum.value != null) {
            updateDrumSize(viewModel.progressRadiusDrum.value!!)
        } else {
            val defaultProgress = 50
            updateDrumSize(defaultProgress)
        }
    }

}