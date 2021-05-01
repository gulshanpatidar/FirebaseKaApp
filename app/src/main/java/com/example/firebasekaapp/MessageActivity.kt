package com.example.firebasekaapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.firebasekaapp.adapters.MessageAdapter
import com.example.firebasekaapp.daos.ChatDao
import com.example.firebasekaapp.daos.UserDao
import com.example.firebasekaapp.databinding.ActivityMessageBinding
import com.example.firebasekaapp.models.Chat
import com.example.firebasekaapp.models.Message
import com.example.firebasekaapp.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MessageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMessageBinding
    private lateinit var userDao: UserDao
    private lateinit var chatDao: ChatDao
    private val currentUserId = Firebase.auth.currentUser!!.uid
    private lateinit var currentUserChat: Chat
    private lateinit var targetUserChat: Chat
    private lateinit var currentUser: User
    private lateinit var targetUser: User
    private lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_message)
        supportActionBar?.title = "Messages"
        userDao = UserDao()
        chatDao = ChatDao()
        val targetUserId = intent.extras?.get("USER_ID")!!.toString()

        binding.sendMessageButton.setOnClickListener{
            sendMessage()
        }

        startChat(targetUserId)
    }

    private fun sendMessage() {
        val textMessage = binding.enterMessage.text.toString()
        binding.enterMessage.setText("")
        addMessage(textMessage)
    }

    private fun startChat(targetUserId: String) {
        val message = binding.enterMessage.text
        GlobalScope.launch {
            currentUser = userDao.getUserById(currentUserId).await().toObject(User::class.java)!!
            targetUser = userDao.getUserById(targetUserId).await().toObject(User::class.java)!!
            val allChats = currentUser.chats
            var isChatExist = false
            for (item in allChats){
                if (item.user2Id==targetUserId){
                    isChatExist = true
                    currentUserChat = item
                    break
                }
            }
            targetUserChat = Chat(currentUserChat.messages,currentUserChat.user2Id,currentUserChat.user2Name,currentUserChat.user2Image,currentUserChat.user1Id,currentUserChat.user1Name,currentUserChat.user1Image)
            if (!isChatExist){
                currentUserChat = Chat(ArrayList(),currentUserId,currentUser.username,currentUser.userImage,targetUserId,targetUser.username,targetUser.userImage)
                targetUserChat = Chat(ArrayList(),targetUserId,targetUser.username,targetUser.userImage,currentUserId,currentUser.username,currentUser.userImage)
                currentUser.chats.add(currentUserChat)
                targetUser.chats.add(targetUserChat)
                userDao.addChatsToUser(currentUser,targetUser)
            }
//            val chat = chatDao.startChat(userId)
            withContext(Dispatchers.Main){
                adapter = MessageAdapter(currentUserChat.messages)
                binding.messageActivityRecyclerView.adapter = adapter
                binding.progressBarMessageActivity.visibility = View.GONE
                supportActionBar?.title = targetUser.username
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
        }
    }

    fun deleteMessage(message: Message, chat: Chat){
        chat.messages.remove(message)
    }

    fun addMessage(text: String){
        val message = Message(text = text, createdBy = currentUserId, createdAt = System.currentTimeMillis(),userImage = currentUser.userImage,userName = currentUser.username)
        currentUser.chats.remove(currentUserChat)
        targetUser.chats.remove(targetUserChat)
        currentUserChat.messages.add(message)
//        targetUserChat.messages.add(message)
        currentUser.chats.add(currentUserChat)
        targetUser.chats.add(targetUserChat)
        userDao.addChatsToUser(currentUser,targetUser)
        adapter.notifyDataSetChanged()
        binding.messageActivityRecyclerView.smoothScrollToPosition(adapter.itemCount)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home ->{
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}