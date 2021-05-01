package com.example.firebasekaapp.daos

import com.example.firebasekaapp.models.Chat
import com.example.firebasekaapp.models.Message
import com.example.firebasekaapp.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChatDao {

//    private val db = FirebaseFirestore.getInstance()
//    private val currentUserId = Firebase.auth.currentUser!!.uid
//    private val userDao = UserDao()
//    private val userCollection = db.collection("users")

//    fun startChat(targetUserId: String) {
//        GlobalScope.launch {
//            val currentUser = userDao.getUserById(currentUserId).await().toObject(User::class.java)!!
//            val targetUser = userDao.getUserById(targetUserId).await().toObject(User::class.java)!!
//            val chat = Chat(
//                user1Id = currentUserId,
//                messages = ArrayList(),
//                user1Name = currentUser.username,
//                user1Image = currentUser.userImage,
//                user2Id = targetUserId,
//                user2Name = targetUser.username,
//                user2Image = targetUser.userImage
//            )
//            currentUser.chats.add(chat)
//            userCollection.document(currentUserId).set(currentUser)
//        }
//    }




}