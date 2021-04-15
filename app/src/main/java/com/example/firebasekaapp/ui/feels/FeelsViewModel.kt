package com.example.firebasekaapp.ui.feels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FeelsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Feels coming soon..."
    }
    val text: LiveData<String> = _text
}