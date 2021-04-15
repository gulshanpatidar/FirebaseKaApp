package com.example.firebasekaapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import coil.load
import coil.transform.CircleCropTransformation
import com.example.firebasekaapp.daos.UserDao
import com.example.firebasekaapp.databinding.ActivityEditProfileBinding
import com.example.firebasekaapp.models.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var userDao: UserDao
    private val PICK_IMAGE_REQUEST = 72
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_edit_profile)

        supportActionBar?.title = "Edit Profile"
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = Firebase.auth
        userDao = UserDao()
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference.child("User Images")

        binding.userImageInEditProfile.setOnClickListener{
            launchGallery()
        }
        binding.changeProfilePhotoText.setOnClickListener {
            launchGallery()
        }
        val currentUserId = auth.currentUser!!.uid
        GlobalScope.launch {
            val user = userDao.getUserById(currentUserId).await().toObject(User::class.java)!!
            withContext(Dispatchers.Main){
                binding.userImageInEditProfile.load(user.userImage){
                    transformations(CircleCropTransformation())
                }
                binding.userNameInEditProfile.setText(user.username)
                binding.userBioInEditProfile.setText(user.bio)
            }
        }
    }

    private fun launchGallery(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,PICK_IMAGE_REQUEST)
    }

    private fun uploadImageToDatabase(){
        val progressBar = ProgressDialog(this)
        progressBar.setMessage("Photo is uploading please wait...")
        progressBar.setTitle("Uploading...")
        progressBar.show()
        if (filePath!=null){
            val fileRef = storageReference!!.child(System.currentTimeMillis().toString())

            val uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(filePath!!)
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot,Task<Uri>> { task->
                if (!task.isSuccessful){
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener{ task ->
                if (task.isSuccessful){
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()
                    userDao.updateProfile(url,binding.userNameInEditProfile.text.toString(),binding.userBioInEditProfile.text.toString())
                    progressBar.dismiss()
                    Toast.makeText(this,"Profile Updated Successfully",Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data?.data != null){
            filePath = data.data
            //getting the path of the image
            binding.userImageInEditProfile.setImageURI(filePath)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_profile,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                //cancel the profile updation process
                finish()
                return true
            }
            R.id.done_button_edit_profile ->{
                //update the profile
                uploadImageToDatabase()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}