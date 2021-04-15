package com.example.firebasekaapp.daos

import android.annotation.SuppressLint
import android.widget.Button
import android.widget.TextView
import com.example.firebasekaapp.R
import com.example.firebasekaapp.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserDao {

    //instance of the database
    private val db = FirebaseFirestore.getInstance()

    //there are many collections. so choose the users collection from there
    private val usersCollection = db.collection("users")

    //create an instance of auth and current user id
    private val currentUserId = Firebase.auth.currentUser!!.uid

    //this method will add user to the database
    fun addUser(user: User?) {
        //just a null check
        user?.let {
            //do this database work in the background thread
            GlobalScope.launch {
                //change its id to userId and add it
                usersCollection.document(user.userId).set(it)
            }
        }
    }

    //this method will return an task instance of the user with the help of the user id
    fun getUserById(uid: String): Task<DocumentSnapshot> {
        return usersCollection.document(uid).get()
    }

    fun updateProfile(url: String, userName: String, userBio: String) {
        getUserById(currentUserId).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = task.result?.toObject(User::class.java)!!
                user.userImage = url
                user.username = userName
                user.bio = userBio
                usersCollection.document(currentUserId).set(user)
            }
        }

    }

    @SuppressLint("ResourceAsColor")
    fun addFollower(targetUserId: String, view: Button,followers: TextView,following: TextView) {
        GlobalScope.launch {
            val currentUser = getUserById(currentUserId).await().toObject(User::class.java)!!
            val targetUser = getUserById(targetUserId).await().toObject(User::class.java)!!
            if (!currentUser.following.contains(targetUserId)) {
                currentUser.following.add(targetUserId)
                targetUser.followers.add(currentUserId)
                withContext(Dispatchers.Main){
                    view.text = R.string.following.toString()
                    view.setBackgroundColor(R.color.material_on_background_disabled)
                    followers.text = targetUser.followers.size.toString()
                    following.text = targetUser.following.size.toString()
                }
            }
            usersCollection.document(currentUserId).set(currentUser)
            usersCollection.document(targetUserId).set(targetUser)
        }
    }

    @SuppressLint("ResourceAsColor")
    fun removeFollower(targetUserId: String, view: Button, followers: TextView, following: TextView) {
        GlobalScope.launch {
            val currentUser = getUserById(currentUserId).await().toObject(User::class.java)!!
            val targetUser = getUserById(targetUserId).await().toObject(User::class.java)!!
            if (currentUser.following.contains(targetUserId)) {
                currentUser.following.remove(targetUserId)
                targetUser.followers.remove(currentUserId)
                withContext(Dispatchers.Main){
                    view.text = R.string.follow.toString()
                    view.setBackgroundColor(R.color.design_default_color_on_primary)
                    followers.text = targetUser.followers.size.toString()
                    following.text = targetUser.following.size.toString()
                }
            }
            usersCollection.document(currentUserId).set(currentUser)
            usersCollection.document(targetUserId).set(targetUser)
        }
    }
}