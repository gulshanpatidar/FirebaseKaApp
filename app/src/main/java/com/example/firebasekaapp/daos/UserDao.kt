package com.example.firebasekaapp.daos

import com.example.firebasekaapp.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserDao {

    //instance of the database
    private val db = FirebaseFirestore.getInstance()
    //there are many collections. so choose the users collection from there
    private val usersCollection = db.collection("users")

    //this method will add user to the database
    fun addUser(user: User?){
        //just a null check
        user?.let {
            //do this database work in the background thread
            GlobalScope.launch {
                //change its id to userId and add it
                usersCollection.document(user.userId).set(it)
            }
        }
    }
}