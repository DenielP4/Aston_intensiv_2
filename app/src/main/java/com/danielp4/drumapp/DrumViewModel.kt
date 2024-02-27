package com.danielp4.drumapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DrumViewModel : ViewModel(){

    val result = MutableLiveData<Int>()
    val lastAngle = MutableLiveData<Float>()
    val progressRadiusDrum = MutableLiveData<Int>()

}