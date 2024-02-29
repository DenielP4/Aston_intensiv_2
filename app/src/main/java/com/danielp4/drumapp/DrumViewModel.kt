package com.danielp4.drumapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
class DrumViewModel : ViewModel(){

    val result = MutableLiveData<Int>()
    val fromAngle = MutableLiveData<Float>()
    val toAngle = MutableLiveData<Float>()
    val progressRadiusDrum = MutableLiveData<Int>()

    val isPaused = MutableLiveData<Boolean>()

    val duration = MutableLiveData<Long>()
}

