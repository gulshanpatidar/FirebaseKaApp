package com.example.firebasekaapp.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.firebasekaapp.adapters.SearchAdapter
import com.example.firebasekaapp.daos.UserDao
import com.example.firebasekaapp.databinding.FragmentSearchBinding
import com.example.firebasekaapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private var users: ArrayList<User>? = null
    private lateinit var userDao: UserDao
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUserId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater)
        binding.lifecycleOwner = this

        val viewModel: SearchViewModel by viewModels()

        binding.viewModel = viewModel

        auth = Firebase.auth
        currentUserId = auth.currentUser!!.uid
        userDao = UserDao()
        binding.searchFragmentRecyclerView.adapter = SearchAdapter()
        return binding.root
    }

//    private fun retrieveAllUsers() {
//        val userCollection = FirebaseFirestore.getInstance().collection("users")
//        GlobalScope.launch {
//            val currentUser =
//                userDao.getUserById(currentUserId).await().toObject(User::class.java)!!
//            val query = userCollection.get().await()
//            val documents = query.documents
//            for (document in documents) {
//                val user = document.toObject(User::class.java)
//                if (user != null && user.userId != currentUserId) {
//                    users?.add(user)
//                }
//            }
//            if (users!!.contains(currentUser)) {
//                users!!.remove(currentUser)
//            }
//            withContext(Dispatchers.Main) {
//                binding.searchFragmentRecyclerView.apply {
//                    adapter = SearchAdapter(requireContext(), users!!)
//                }
//            }
//        }
//    }
}