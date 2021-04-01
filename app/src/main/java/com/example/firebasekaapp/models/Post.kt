package com.example.firebasekaapp.models

data class Post(val text: String="",val imageUrl: String = "", val createdBy: User = User(),val createdAt: Long = 0L,val likedBy:ArrayList<String> = ArrayList(),val comments: ArrayList<Comment> = ArrayList())