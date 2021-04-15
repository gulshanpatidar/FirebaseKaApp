package com.example.firebasekaapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebasekaapp.*
import com.example.firebasekaapp.adapters.IPostAdapter
import com.example.firebasekaapp.adapters.PostAdapter
import com.example.firebasekaapp.daos.PostDao
import com.example.firebasekaapp.databinding.FragmentHomeBinding
import com.example.firebasekaapp.models.Post
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class HomeFragment : Fragment(), IPostAdapter {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: PostAdapter
    private lateinit var postDao: PostDao

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        auth = Firebase.auth

        postDao = PostDao()
        setupRecyclerView()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.activity_main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logOutMenuItem) {
            auth.signOut()
            Toast.makeText(context, "You have been successfully logged out", Toast.LENGTH_LONG).show()
            val intent = Intent(context, WelcomeActivity::class.java)
            startActivity(intent)
//            activity?.supportFragmentManager?.beginTransaction()?.remove(this)
            activity?.finish()
            return true
        } else if (item.itemId == R.id.addPostButton) {
            val intent = Intent(context, CreatePostActivity::class.java)
            startActivity(intent)
//            activity?.supportFragmentManager?.beginTransaction()?.remove(this)
            activity?.finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerView() {
        val postCollection = postDao.postCollection
        val query = postCollection.orderBy("createdAt", Query.Direction.DESCENDING)
        val recyclerViewOptions =
            FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post::class.java).build()

        adapter = PostAdapter(recyclerViewOptions, this)

        binding.recyclerViewMain.adapter = adapter
        binding.recyclerViewMain.layoutManager = LinearLayoutManager(context)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onLikeClicked(postId: String, isDoubleClicked: Boolean) {
        postDao.updateLikes(postId,isDoubleClicked)
    }

    override fun onCommentClicked(postId: String, text: String) {
        postDao.addComment(text, postId)
    }

    override fun onViewCommentClicked(postId: String) {
        val intent = Intent(requireActivity(), CommentActivity::class.java)
        intent.putExtra("postId",postId)
        startActivity(intent)
    }

    override fun onImageDoubleClicked() {
        Toast.makeText(context,"You Just Liked it!!!",Toast.LENGTH_SHORT).show()
    }

    override fun onUserClicked(postId: String) {
        GlobalScope.launch {
            val post = postDao.getPostById(postId).await().toObject(Post::class.java)!!
            val userId = post.createdBy.userId
            withContext(Dispatchers.Main){
//                val actionDetails = HomeFragmentDirections.actionNavigationHomeToNavigationProfile()
                findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToNavigationProfile(userId))
            }
        }
    }
}