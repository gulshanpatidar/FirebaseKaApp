package com.example.firebasekaapp.models

data class User(val userId: String = "",var username: String = "",val email: String = "",var userImage: String? ="",val followers: ArrayList<String> = ArrayList(),val following: ArrayList<String> = ArrayList(),var bio: String = "",val posts: ArrayList<String> = ArrayList())