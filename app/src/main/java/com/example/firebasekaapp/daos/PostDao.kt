package com.example.firebasekaapp.daos

import android.net.Uri
import com.example.firebasekaapp.models.Comment
import com.example.firebasekaapp.models.Post
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.example.firebasekaapp.models.User

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
            //create an instance of the post
            val post = Post(text,image,user,currentTime)
            //create this post in the firebase
            postCollection.document().set(post)
        }
    }

    //this method will take the post id and then return the task of the post that can be converted into the post later on
    private fun getPostById(postId: String): Task<DocumentSnapshot>{
        return postCollection.document(postId).get()
    }

    //this method will update the likes on the post by taking the post id as a argument
    fun updateLikes(postId: String){
        //do this task in the background thread
        GlobalScope.launch {
            //get the current user id who is liking the post
            val currentUserId = auth.currentUser!!.uid
            //get that post
            val post = getPostById(postId).await().toObject(Post::class.java)!!
            //create a boolean for like button
            val isLiked = post.likedBy.contains(currentUserId)
            if (isLiked){
                post.likedBy.remove(currentUserId)
            }else{
                post.likedBy.add(currentUserId)
            }
            //update the post in the firebase now
            postCollection.document(postId).set(post)
        }
    }

    fun addComment(text: String,postId: String){
        //now do the rest database task in the background
        GlobalScope.launch {
            //get the user id
            val currentUserId = auth.currentUser!!.uid
            //create instance of the user dao so that we can use it's methods
            val userDao = UserDao()
            //get the user task and convert it to user object
            val user = userDao.getUserById(currentUserId).await().toObject(User::class.java)!!
            //get the current time from the system
            val currentTime = System.currentTimeMillis()
            //get the post task and convert it to post object
            val post = getPostById(postId).await().toObject(Post::class.java)!!
            //create the instance of the comment
            val comment = Comment(text,user,currentTime)
            //add this comment to the post
            post.comments.add(comment)
            //update this post in the database
            postCollection.document(postId).set(post)
        }
    }
}