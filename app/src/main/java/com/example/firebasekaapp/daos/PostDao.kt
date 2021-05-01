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

class PostDao {

    //create instance of the database
    private val db = FirebaseFirestore.getInstance()

    //get the post collection
    val postCollection = db.collection("posts")

    //get the user collection
    private val userCollection = db.collection("users")

    //create an auth instance for finding the user data
    private val auth = Firebase.auth

    //this method will take image and text to add post to the database
    fun addPost(text: String, image: String) {
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
            val post = Post(text = text,imageUrl =  image,createdBy =  user,createdAt =  currentTime)
            //add this post to the user
//            user.posts.add()
            //create this post in the firebase
//            val reference = postCollection.document().set(post).await()
            val documentId = postCollection.add(post).await().id
            //update the user in the firebase
            user.posts.add(documentId)
            userCollection.document(currentUserId).set(user)
        }
//        postCollection.addSnapshotListener{snapshot,e ->
//            //if there is an exception
//            if (e!=null){
//                //return
//                return@addSnapshotListener
//            }
//            if (snapshot!=null){
//                val allPosts = ArrayList<Post>()
//                val documents = snapshot.documents
//                documents.forEach{
//                    val post = it.toObject(Post::class.java)
//                    if (post!=null){
//                        post.postId = it.id
//                        allPosts.add(post)
//                    }
//                }
//            }
//        }
    }

    //this method will take the post id and then return the task of the post that can be converted into the post later on
    fun getPostById(postId: String): Task<DocumentSnapshot> {
        return postCollection.document(postId).get()
    }

    //this method will update the likes on the post by taking the post id as a argument
    fun updateLikes(postId: String, isDoubleClicked: Boolean) {
        //do this task in the background thread
        GlobalScope.launch {
            //get the current user id who is liking the post
            val currentUserId = auth.currentUser!!.uid
            //get that post
            val post = getPostById(postId).await().toObject(Post::class.java)!!
            //create a boolean for like button
            val isLiked = post.likedBy.contains(currentUserId)
            if (isLiked) {
                if (isDoubleClicked) {
                    //do nothing here
                } else {
                    post.likedBy.remove(currentUserId)
                }
            } else {
                post.likedBy.add(currentUserId)
            }
            //update the post in the firebase now
            postCollection.document(postId).set(post)
        }
    }

    fun addComment(text: String, postId: String) {
        val currentUserId = auth.currentUser!!.uid
        GlobalScope.launch {
            val userDao = UserDao()

            //get the user and post both
            val user = userDao.getUserById(currentUserId).await().toObject(User::class.java)!!
            val post = getPostById(postId).await().toObject(Post::class.java)!!
            val currentTime = System.currentTimeMillis()
            //create instance of the comment and upload it in the database
            val comment = Comment(text = text, commentedBy = user, commentedAt = currentTime)
            post.comments.add(comment)
            postCollection.document(postId).set(post)
        }
    }
}