package com.example.firebasekaapp.daos

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PostDao {

    //create instance of the database
    private val db = FirebaseFirestore.getInstance()
    //get the post collection
    val postCollection = db.collection("posts")
    //create an auth instance for finding the user data
    private val auth = Firebase.auth

    //this method will take image and text to add post to the database
    fun addPost(text: String,image: String){
        //get the current user id
        val currentUserId = auth.currentUser!!.uid
        //get the user instance with the help of id in the background thread
        GlobalScope.launch {
            //create instance of the user dao so that we can use its methods
            val userDao = UserDao()
            //get the user instance with the help of dao's method and assert that this user will not be null
            val user = userDao.getUserById(currentUserId).await().toObject(User::class.java)!!
            //get the current time from the system
            val currentTime = System.currentTimeMillis()
            //set the image and do the remaining procedure
        }
    }


}