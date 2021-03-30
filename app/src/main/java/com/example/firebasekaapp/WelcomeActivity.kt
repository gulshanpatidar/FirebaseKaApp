package com.example.firebasekaapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.firebasekaapp.databinding.ActivityWelcomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_welcome)

        //set the on click listeners
        binding.signInActivityButton.setOnClickListener{
            val intent = Intent(this,SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.signUpActivityButton.setOnClickListener{
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

        //initialize the auth
        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()
        //check if the user is signed in and update the UI accordingly
        val currentUser = auth.currentUser
        if (currentUser!=null){
            updateUI()
        }
    }

    private fun updateUI() {
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}