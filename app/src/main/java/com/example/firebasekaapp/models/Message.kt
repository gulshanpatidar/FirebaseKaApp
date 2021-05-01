package com.example.firebasekaapp.models

data class Message(val text: String="", val image: String="",val createdBy: String = "",val createdAt: Long = 0L,val userImage: String? = "",val userName: String = "")