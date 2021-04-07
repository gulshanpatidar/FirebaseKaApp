package com.example.firebasekaapp.ui.feels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FeelsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Feels Fragment"
    }
    val text: LiveData<String> = _text
}