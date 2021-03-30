package com.example.firebasekaapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.firebasekaapp.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_sign_in)

        //initialize the auth
        auth = Firebase.auth

        binding.signInButton.setOnClickListener {
            val email = binding.signInEmail.text.toString()
            val password = binding.signInPassword.text.toString()

            val isEmailValid = Utils.isEmailValid(email)
            if (isEmailValid){
                signIn(email,password)
            }else{
                Toast.makeText(this,"Please Enter valid email address",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(this){ task ->
                if (task.isSuccessful){
                    //sign in success
                    val user = auth.currentUser
                    updateUI(user)
                }else{
                    //sign in failed
                    Toast.makeText(this,"Authentication failed error.",Toast.LENGTH_LONG).show()
                    updateUI(null)
                }
            }
    }

    //this method is used to customize the back button
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this,WelcomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    //this method is used to customize the up button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            android.R.id.home -> {
                val intent = Intent(this,WelcomeActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    //this method is used to update the UI
    private fun updateUI(user: FirebaseUser?) {
        if (user!=null){

            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }else{

        }
    }
}