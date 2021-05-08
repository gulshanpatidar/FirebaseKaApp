package com.example.firebasekaapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasekaapp.adapters.MessageAdapter
import com.example.firebasekaapp.daos.UserDao
import com.example.firebasekaapp.models.Chat
import com.example.firebasekaapp.models.Message
import com.example.firebasekaapp.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MessageViewModel(targetUserId: String) : ViewModel() {

    private val currentUserId = Firebase.auth.currentUser!!.uid
    private lateinit var currentUser: User
    private lateinit var targetUser: User
    private lateinit var currentUserChat: Chat
    private lateinit var targetUserChat: Chat
    private val userDao = UserDao()
    private val currentUserRef = userDao.usersCollection.document(currentUserId)
    private val targetUserRef = userDao.usersCollection.document(targetUserId)
    private val adapter = MessageAdapter()
    var messageText: String? = null
    private val _clearEditText = MutableLiveData<Boolean>()
    val clearEditText: LiveData<Boolean>
        get() = _clearEditText


    init {
        startChat(targetUserId)
    }

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>>
        get() = _messages

    fun startChat(targetUserId: String) {

        var isChatExist = false

        targetUserRef.addSnapshotListener{snapshot, e->
            e?.let {
                //this is an exception
                return@addSnapshotListener
            }
            snapshot?.let {
                targetUser = it.toObject(User::class.java)!!
            }
        }

        currentUserRef.addSnapshotListener{snapshot, e ->
            e?.let {
                //this is an error
                return@addSnapshotListener
            }
            snapshot?.let {
                currentUser = it.toObject(User::class.java)!!

                val allChats = currentUser.chats
                currentUserChat = allChats[0]
                for (item in allChats) {
                    if (item.user2Id == targetUserId) {
                        isChatExist = true
                        currentUserChat = item
                        break
                    }
                }
                targetUserChat = Chat(
                    currentUserChat.messages,
                    currentUserChat.user2Id,
                    currentUserChat.user2Name,
                    currentUserChat.user2Image,
                    currentUserChat.user1Id,
                    currentUserChat.user1Name,
                    currentUserChat.user1Image
                )
                if (!isChatExist) {
                    currentUserChat = Chat(
                        ArrayList(),
                        currentUserId,
                        currentUser.username,
                        currentUser.userImage,
                        targetUserId,
                        targetUser.username,
                        targetUser.userImage
                    )
                    targetUserChat = Chat(
                        ArrayList(),
                        targetUserId,
                        targetUser.username,
                        targetUser.userImage,
                        currentUserId,
                        currentUser.username,
                        currentUser.userImage
                    )
                    if (this::currentUserChat.isInitialized){
                        currentUser.chats.removeAt(currentUser.chats.size -1)
                        targetUser.chats.removeAt(targetUser.chats.size -1)
                    }
                    currentUser.chats.add(currentUserChat)
                    targetUser.chats.add(targetUserChat)
                    userDao.addChatsToUser(currentUser, targetUser)
                }
                Log.d("MessagesHaiYe", currentUserChat.messages.toString())
                _messages.value = currentUserChat.messages
                Log.d("MessagesHaiYe", messages.value.toString())
            }
        }
    }

    fun deleteMessage(message: Message, chat: Chat) {
        chat.messages.remove(message)
    }

    fun addMessage(text: String) {
        val message = Message(
            text = text,
            createdBy = currentUserId,
            createdAt = System.currentTimeMillis(),
            userImage = currentUser.userImage,
            userName = currentUser.username
        )
        currentUser.chats.remove(currentUserChat)
        targetUser.chats.remove(targetUserChat)
        currentUserChat.messages.add(message)
//        targetUserChat.messages.add(message)
        currentUser.chats.add(currentUserChat)
        targetUser.chats.add(targetUserChat)
        userDao.addChatsToUser(currentUser, targetUser)
        _messages.value = currentUserChat.messages
    }

    fun sendMessage() {
        addMessage(messageText!!)
        _clearEditText.value = true
    }
}