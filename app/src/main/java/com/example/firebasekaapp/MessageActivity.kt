package com.example.firebasekaapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.firebasekaapp.adapters.MessageAdapter
import com.example.firebasekaapp.daos.ChatDao
import com.example.firebasekaapp.daos.UserDao
import com.example.firebasekaapp.databinding.ActivityMessageBinding
import com.example.firebasekaapp.models.Chat
import com.example.firebasekaapp.models.Message
import com.example.firebasekaapp.models.User
import com.example.firebasekaapp.viewmodels.MessageViewModel
import com.example.firebasekaapp.viewmodels.MessageViewModelFactory
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MessageActivity : AppCompatActivity() {

    //ab mujhe is activity ke liye ek viewModel banana padega jiske andar ek live data banega or uski ki help se message ke data ko observe kiya jaayega or accordingly action liya jaayega. or bhi main is app ko efficient banane ke liye mvvm architecture ka use karne vala hu to bas dekhte jaaiye ki kya kya hota he yaha pe.

    private lateinit var binding: ActivityMessageBinding
    private lateinit var adapter: MessageAdapter
    private lateinit var viewModel: MessageViewModel
    private lateinit var viewModelFactory: MessageViewModelFactory
    private lateinit var fromWhere: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_message)
        binding.lifecycleOwner = this
        supportActionBar?.title = "Messages"
        val targetUserId = intent.extras?.get("USER_ID")!!.toString()
        fromWhere = intent.extras?.get("FROM_WHERE")!!.toString()

        viewModelFactory = MessageViewModelFactory(targetUserId)
        viewModel = ViewModelProvider(this,viewModelFactory).get(MessageViewModel::class.java)
        binding.viewModel = viewModel
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        adapter = MessageAdapter()
        binding.messageActivityRecyclerView.adapter = adapter
        viewModel.messages.observe(this, Observer {
            adapter.notifyDataSetChanged()
            binding.messageActivityRecyclerView.smoothScrollToPosition(it.size)
        })
        viewModel.clearEditText.observe(this, Observer {
            binding.enterMessage.setText("")
            it.and(false)
        })
    }
    override fun onBackPressed() {
        super.onBackPressed()
        if (fromWhere=="ChatActivity"){
            val intent = Intent(this,ChatActivity::class.java)
            startActivity(intent)
        }
        else{
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home ->{
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}