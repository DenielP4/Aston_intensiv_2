package com.danielp4.drumapp

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.danielp4.drumapp.Constants.IMAGE_URL
import com.danielp4.drumapp.Constants.TEXT
import com.danielp4.drumapp.databinding.ActivityMainBinding
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlin.math.abs
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    var isAnimating = false
    private val sweepAngle = 360f / Constants.rainbow.keys.size
    var resultAngle = 0f

    var timeStartAnim: Long = 0

    private var animator: ObjectAnimator? = null

    private val viewModel: DrumViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupSeekBarListener()
        binding.apply {
            bStart.setOnClickListener {
                isAnimating = true
                startDrum()
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
        viewModel.duration.value = Random.nextLong(5000, 7001)
        viewModel.toAngle.value = Random.nextInt(360, 720).toFloat()
        if (drumView.rotation != 0f) {
            drumView.rotation = 0f
        }
        val fromAngle = if (checkAngle(viewModel.fromAngle.value)) 0f else viewModel.fromAngle.value!!
        viewModel.fromAngle.value = viewModel.toAngle.value!! % 360
        animator = ObjectAnimator.ofFloat(binding.drumView, "rotation", fromAngle, viewModel.toAngle.value!!).apply {
            duration = viewModel.duration.value!!
            interpolator = LinearInterpolator()
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator) {
                    timeStartAnim = SystemClock.elapsedRealtime()
                    viewModel.isPaused.value = false
                }
                override fun onAnimationEnd(p0: Animator) {
                    isAnimating = false
                    resultAngle = 360 - (viewModel.toAngle.value!! % 360)
                    getItem(resultAngle)
                }
                override fun onAnimationCancel(p0: Animator) {}
                override fun onAnimationRepeat(p0: Animator) {}
            })
            addPauseListener(object : Animator.AnimatorPauseListener {
                override fun onAnimationPause(p0: Animator) {
                    viewModel.fromAngle.value = drumView.rotation
                    viewModel.isPaused.value = true
                    viewModel.duration.value = viewModel.duration.value!! - (SystemClock.elapsedRealtime() - timeStartAnim)
                }
                override fun onAnimationResume(p0: Animator) {}
            })
        }
        animator?.start()
    }

    private fun resumeDrum() = with(binding){
        if (drumView.rotation != 0f) {
            drumView.rotation = 0f
        }
        viewModel.fromAngle.value = viewModel.toAngle.value!! % 360
        animator = ObjectAnimator.ofFloat(binding.drumView, "rotation", viewModel.fromAngle.value!!, viewModel.toAngle.value!!).apply {
            duration = viewModel.duration.value!!
            interpolator = LinearInterpolator()
            addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
                override fun onAnimationUpdate(p0: ValueAnimator) {
                }
            })
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator) {
                    timeStartAnim = SystemClock.elapsedRealtime()
                    viewModel.isPaused.value = false
                }
                override fun onAnimationEnd(p0: Animator) {
                    isAnimating = false
                    resultAngle = 360 - (viewModel.toAngle.value!! % 360)
                    getItem(resultAngle)
                }
                override fun onAnimationCancel(p0: Animator) {}
                override fun onAnimationRepeat(p0: Animator) {}
            })
            addPauseListener(object : Animator.AnimatorPauseListener {
                override fun onAnimationPause(p0: Animator) {
                    viewModel.fromAngle.value = drumView.rotation
                    viewModel.isPaused.value = true
                    viewModel.duration.value = viewModel.duration.value!! - (SystemClock.elapsedRealtime() - timeStartAnim)
                }
                override fun onAnimationResume(p0: Animator) {}
            })
        }
        animator?.start()

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
            }
            TEXT -> {
                imView.visibility = View.GONE
                finalTextView.visibility = View.VISIBLE
            }
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

    private fun updateDrumSize(progress: Int) = with(binding) {
        val newRadius = calculateNewRadius(progress)
        drumView.apply {
            updateRadius(newRadius)
        }
    }

    private fun setupSeekBarListener() {
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateDrumSize(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                viewModel.progressRadiusDrum.value = seekBar?.progress
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.isPaused.value == true){
            binding.drumView.rotation = viewModel.fromAngle.value!!
            resumeDrum()
        } else {
            if (!checkAngle(viewModel.fromAngle.value)) {
                val angle = 360 - (viewModel.fromAngle.value!! % 360)
                getItem(angle)
                binding.drumView.rotation = viewModel.fromAngle.value!!
            }
        }
        if (viewModel.progressRadiusDrum.value != null) {
            updateDrumSize(viewModel.progressRadiusDrum.value!!)
        } else {
            val defaultProgress = 50
            updateDrumSize(defaultProgress)
        }
    }



    override fun onStop() {
        super.onStop()
        animator?.pause()
    }
}