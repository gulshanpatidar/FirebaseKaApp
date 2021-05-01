package com.example.firebasekaapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.firebasekaapp.daos.PostDao
import com.example.firebasekaapp.databinding.ActivityCreatePostBinding
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask

class CreatePostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePostBinding
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private lateinit var postDao: PostDao
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_post)

        supportActionBar?.title = "Add Post"

        //initially hide the image view and show the button
        binding.newImagePost.visibility = View.GONE
        binding.addImagePostButton.visibility = View.VISIBLE
        binding.addPostButtonCreatePost.isEnabled = false

        postDao = PostDao()
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference.child("Post Images")

        binding.addImagePostButton.setOnClickListener {
            launchGallery()
        }

        binding.addPostButtonCreatePost.setOnClickListener {
            uploadImageToDatabase()
        }
    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun uploadImageToDatabase() {
        val progressBar = ProgressDialog(this)
        progressBar.setMessage("Image is uploading please wait.")
        progressBar.setTitle("Uploading...")
        progressBar.show()

        if (filePath != null) {
            val fileRef = storageReference!!.child(System.currentTimeMillis().toString())

            val uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(filePath!!)
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    postDao.addPost(binding.addPostCaption.text.toString(), url)
                    progressBar.dismiss()
                    val intent = Intent(this, MainActivity::class.java)
//                    startActivity(intent)
//                    onBackPressed()
                    finish()
                    Toast.makeText(this, "Post Uploaded Successfully!!!", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data?.data != null) {
            filePath = data.data
        }
        binding.addImagePostButton.visibility = View.GONE
        binding.newImagePost.visibility = View.VISIBLE
        binding.newImagePost.setImageURI(filePath)
        binding.addPostButtonCreatePost.isEnabled = true
    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//        val intent = Intent(this, MainActivity::class.java)
//        startActivity(intent)
//        finish()
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            android.R.id.home -> {
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}