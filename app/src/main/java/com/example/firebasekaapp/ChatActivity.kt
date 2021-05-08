package com.example.firebasekaapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebasekaapp.adapters.ChatAdapter
import com.example.firebasekaapp.adapters.IChatAdapter
import com.example.firebasekaapp.daos.UserDao
import com.example.firebasekaapp.databinding.ActivityChatBinding
import com.example.firebasekaapp.models.Chat
import com.example.firebasekaapp.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ChatActivity : AppCompatActivity(), IChatAdapter {

    private lateinit var binding: ActivityChatBinding
    private val currentUserId = Firebase.auth.currentUser!!.uid
    private lateinit var userDao: UserDao
    private lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_chat)

        supportActionBar!!.title = "Chats"
        userDao = UserDao()
        setupRecyclerView()
    }

    private fun setupRecyclerView(){
        GlobalScope.launch {
            val currentUser = userDao.getUserById(currentUserId).await().toObject(User::class.java)!!
            val chats = currentUser.chats
            withContext(Dispatchers.Main){
                 adapter = ChatAdapter(chats,this@ChatActivity)
                binding.chatActivityRecyclerView.adapter = adapter
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==android.R.id.home){
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onChatClicked(chat: Chat) {
        val intent = Intent(this,MessageActivity::class.java)
        val targetUserId = chat.user2Id
        intent.putExtra("USER_ID",targetUserId)
        intent.putExtra("FROM_WHERE","ChatActivity")
        startActivity(intent)
        finish()
    }
}