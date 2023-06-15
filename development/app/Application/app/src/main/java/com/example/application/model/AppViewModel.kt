package com.example.application.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AppViewModel : ViewModel() {
    private val _username = MutableLiveData<String>()
    val username: LiveData<String> = _username

    private val _lockID = MutableLiveData<Int>()
    val lockID: LiveData<Int> = _lockID

    fun setUsername(name : String){
        _username.value = name
    }

    fun setLockID(name : Int){
        _lockID.value = name
    }

    init {
        // Set initial values for the order
        resetOrder()
    }

    private fun resetOrder() {
        _username.value = ""
    }

}