package com.example.firebasekaapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebasekaapp.adapters.IPostAdapter
import com.example.firebasekaapp.adapters.PostAdapter
import com.example.firebasekaapp.daos.CommentDao
import com.example.firebasekaapp.daos.PostDao
import com.example.firebasekaapp.databinding.ActivityMainBinding
import com.example.firebasekaapp.models.Post
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(), IPostAdapter {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: PostAdapter
    private lateinit var postDao: PostDao
    private lateinit var commentDao: CommentDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        auth = Firebase.auth

        postDao = PostDao()
        commentDao =  CommentDao()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val postCollection = postDao.postCollection
        val query = postCollection.orderBy("createdAt", Query.Direction.DESCENDING)
        val recyclerViewOptions =
            FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post::class.java).build()

        adapter = PostAdapter(recyclerViewOptions, this)

        binding.recyclerViewMain.adapter = adapter
        binding.recyclerViewMain.layoutManager = LinearLayoutManager(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logOutMenuItem) {
            auth.signOut()
            Toast.makeText(this, "You have been successfully logged out", Toast.LENGTH_LONG).show()
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
            return true
        } else if (item.itemId == R.id.addPostButton) {
            val intent = Intent(this, CreatePostActivity::class.java)
            startActivity(intent)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onLikeClicked(postId: String) {
        postDao.updateLikes(postId)
    }

    override fun onCommentClicked(postId: String, text: String) {
        commentDao.addComment(text, postId)
    }

    override fun onViewCommentClicked(postId: String) {
        val intent = Intent(this,CommentActivity::class.java)
        intent.putExtra("postId",postId)
        startActivity(intent)
    }
}