package com.example.firebasekaapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.firebasekaapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        auth = Firebase.auth

        binding.helloText.text = "Hello ${auth.currentUser.displayName}"

        binding.signOutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(this,WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}