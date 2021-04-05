package com.example.firebasekaapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.firebasekaapp.adapters.CommentAdapter
import com.example.firebasekaapp.daos.CommentDao
import com.example.firebasekaapp.daos.PostDao
import com.example.firebasekaapp.databinding.ActivityCommentBinding
import com.example.firebasekaapp.models.Comment
import com.example.firebasekaapp.models.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CommentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommentBinding
    private lateinit var adapter: CommentAdapter
    private lateinit var commentDao: CommentDao
    private lateinit var postId: String
    private lateinit var comments: List<Comment>
    private lateinit var postDao: PostDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_comment)

        supportActionBar?.title = "Comments"

        commentDao = CommentDao()
        postDao = PostDao()

        postId = intent.getStringExtra("postId")!!
        getComments()
//        setUpRecyclerView()
    }

    private fun getComments() {
        GlobalScope.launch {
            val post = postDao.getPostById(postId).await().toObject(Post::class.java)!!
            comments = post.comments
            withContext(Dispatchers.Main) {
                adapter = CommentAdapter(this@CommentActivity, comments, postId)
                binding.recyclerViewComment.adapter = adapter
            }
        }
    }

//    private fun setUpRecyclerView() {
//        val commentCollection = commentDao.commentCollection
//        val query = commentCollection.whereEqualTo("commentedOn", postId)
////            .orderBy(
////            "commentedAt",
////            Query.Direction.DESCENDING
////        )
//
//        val recyclerViewOptions =
//            FirestoreRecyclerOptions.Builder<Comment>().setQuery(query, Comment::class.java).build()
//
//        adapter = CommentAdapter(this,comments,postId)
//
//        binding.recyclerViewComment.adapter = adapter
//    }
}