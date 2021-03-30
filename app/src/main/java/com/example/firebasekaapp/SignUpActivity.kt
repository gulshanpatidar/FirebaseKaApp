package com.example.firebasekaapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.firebasekaapp.daos.UserDao
import com.example.firebasekaapp.databinding.ActivitySignUpBinding
import com.example.firebasekaapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)

        //initialize the auth
        auth = Firebase.auth

        //set the click listener for the sign up button
        binding.signUpButton.setOnClickListener {
            val email = binding.signUpEmail.text.toString()
            val password = binding.signUpPassword.text.toString()

            val isEmailValid = Utils.isEmailValid(email)
            if (isEmailValid) {
                signUp(email, password)
            } else {
                Toast.makeText(this, "Please Enter Valid email address", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun signUp(email: String, password: String) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){ task ->
                if (task.isSuccessful){
                    //sign in success
                    val user = auth.currentUser
                    updateProfile(user)
                }else{
                    //sign in fails
                    Toast.makeText(this,"Authentication Failed",Toast.LENGTH_LONG).show()
                    updateUI(null)
                }
            }
    }

    private fun updateProfile(firebaseUser: FirebaseUser?){
        //update the displayName of the user
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(binding.signUpUsername.text.toString())
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            firebaseUser?.updateProfile(profileUpdates)?.await()
            withContext(Dispatchers.Main){
                updateUI(firebaseUser)
            }
        }
    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        if (firebaseUser!=null){

            //create an instance of the user so that we can store it in database
            val user = User(firebaseUser.uid,firebaseUser.displayName!!,firebaseUser.email!!)
            //create instance of the dao to add the user
            val userDao = UserDao()
            //add the user
            userDao.addUser(user)

            //start the main activity
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }else{

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, WelcomeActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}