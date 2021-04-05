package com.example.firebasekaapp.daos

import com.example.firebasekaapp.models.Comment
import com.example.firebasekaapp.models.Post
import com.example.firebasekaapp.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CommentDao {

    //get the instance of database
    private val db = FirebaseFirestore.getInstance()
    //get the comment collection
    val commentCollection = db.collection("comments")
    //create an instance of the auth to find the user data
    private val auth = Firebase.auth

    fun addComment(text: String,postId: String){
        val currentUserId = auth.currentUser!!.uid
        GlobalScope.launch {
            val userDao = UserDao()

            //get the user and post both
            val user = userDao.getUserById(currentUserId).await().toObject(User::class.java)!!
            val currentTime = System.currentTimeMillis()
            //create instance of the comment and upload it in the database
            val comment = Comment(text = text,commentedBy = user,commentedAt = currentTime,commentedOn = postId)
            commentCollection.document().set(comment)
        }
    }

    fun getCommentById(commentId: String): Task<DocumentSnapshot>{
        return commentCollection.document(commentId).get()
    }
}