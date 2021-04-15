package com.example.firebasekaapp.ui.activities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ActivitiesViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "Activities coming soon..."
    }
    val text: LiveData<String> = _text
}