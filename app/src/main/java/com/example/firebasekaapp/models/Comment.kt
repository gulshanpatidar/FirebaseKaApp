package com.example.firebasekaapp.models

data class Comment(val text: String = "",val commentedBy: User = User(),val commentedAt: Long = 0L)