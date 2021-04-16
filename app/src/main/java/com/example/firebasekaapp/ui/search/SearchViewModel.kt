package com.example.firebasekaapp.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasekaapp.daos.UserDao
import com.example.firebasekaapp.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception

enum class UserStatus{LOADING,ERROR, DONE}

class SearchViewModel : ViewModel() {

    private val currentUserId = Firebase.auth.currentUser!!.uid
    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>>
    get() = _users

    private val _status  = MutableLiveData<UserStatus>()
    val status: LiveData<UserStatus> = _status

    private var mUsers: ArrayList<User> = ArrayList()

    init {
        retrieveAllUsers()
    }

    private fun retrieveAllUsers(){
        val userCollection = FirebaseFirestore.getInstance().collection("users")
        viewModelScope.launch {
            _status.value = UserStatus.LOADING
            try {
                val query = userCollection.get().await()
                val documents = query.documents
                for (document in documents) {
                    val user = document.toObject(User::class.java)
                    if (user != null && user.userId != currentUserId) {
                        mUsers.add(user)
                    }
                }
                _users.value = mUsers
                _status.value = UserStatus.DONE
            }catch (e: Exception){
                _status.value = UserStatus.ERROR
                _users.value = ArrayList()
            }
        }
    }
}