package com.example.firebasekaapp.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.firebasekaapp.adapters.ISearchAdapter
import com.example.firebasekaapp.adapters.SearchAdapter
import com.example.firebasekaapp.daos.UserDao
import com.example.firebasekaapp.databinding.FragmentSearchBinding
import com.example.firebasekaapp.ui.home.HomeFragmentDirections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SearchFragment : Fragment(),ISearchAdapter {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var userDao: UserDao
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUserId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater)
        binding.lifecycleOwner = this

        val viewModel: SearchViewModel by viewModels()

        binding.viewModel = viewModel

        auth = Firebase.auth
        currentUserId = auth.currentUser!!.uid
        userDao = UserDao()
        binding.searchFragmentRecyclerView.adapter = SearchAdapter(this)
        return binding.root
    }

    override fun onUserClick(targetUserId: String) {
        findNavController().navigate(SearchFragmentDirections.actionNavigationSearchToNavigationProfile(targetUserId))
    }
}