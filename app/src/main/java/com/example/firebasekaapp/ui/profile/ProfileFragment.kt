package com.example.firebasekaapp.ui.profile

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import coil.load
import coil.transform.CircleCropTransformation
import com.example.firebasekaapp.models.User
import com.example.firebasekaapp.EditProfileActivity
import com.example.firebasekaapp.R
import com.example.firebasekaapp.daos.UserDao
import com.example.firebasekaapp.databinding.ProfileFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: ProfileFragmentBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userDao: UserDao

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        binding = ProfileFragmentBinding.inflate(inflater, container, false)
//        viewModel.text.observe(viewLifecycleOwner, Observer {
//            binding.textProfile.text = it
//        })
        auth = Firebase.auth
        userDao = UserDao()

        val args = ProfileFragmentArgs.fromBundle(requireArguments())
        var userId = args.userId

        val currentUserId = auth.currentUser!!.uid

        if (userId!!.isNotEmpty() && userId!=currentUserId){
            binding.editProfileButton.text = R.string.follow.toString()
            binding.savedPostButton.text = R.string.message.toString()
            binding.editProfileButton.setOnClickListener {
                userDao.addFollower(userId!!,binding.editProfileButton,binding.numberOfFollowersInProfile,binding.numberOfFollowingInProfile)
            }
            binding.savedPostButton.setOnClickListener {
                Toast.makeText(context,"Ruko Zara Sabr karo!!!",Toast.LENGTH_SHORT).show()
            }
        }
        else{
            binding.editProfileButton.setOnClickListener {
                val intent = Intent(context,EditProfileActivity::class.java)
                startActivity(intent)
            }
            userId = currentUserId
        }


        GlobalScope.launch {
            val user = userDao.getUserById(userId).await().toObject(User::class.java)!!
            withContext(Dispatchers.Main){
                binding.numberOfPostInProfile.text = user.posts.size.toString()
                binding.numberOfFollowersInProfile.text = user.followers.size.toString()
                binding.numberOfFollowingInProfile.text = user.following.size.toString()
                binding.userNameInProfile.text = user.username
                binding.userBioInProfile.text = user.bio
                if (user.followers.contains(currentUserId)){
                    binding.editProfileButton.text = R.string.following.toString()
                    binding.editProfileButton.setBackgroundColor(R.color.material_on_background_disabled)
                    binding.editProfileButton.setOnClickListener {
                        showAlertDialog(userId)
                    }
                }
//                Glide.with(binding.userImageInProfile.context).load(user.userImage).circleCrop().into(binding.userImageInProfile)
                binding.userImageInProfile.load(user.userImage){
                    transformations(CircleCropTransformation())
                }
            }
        }

        return binding.root
    }

    private fun showAlertDialog(userId: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Unfollow User")
        builder.setMessage("Are you sure you want to unfollow this user?")

        //performing positive action
        builder.setPositiveButton("Yes"){dialogInterface, which ->
            //do nothing for now
            userDao.removeFollower(userId,binding.editProfileButton,binding.numberOfFollowersInProfile,binding.numberOfFollowingInProfile)
        }

        builder.setNegativeButton("No"){dialogInterface,which ->
            //do nothing for now
        }

        val alertDialog: AlertDialog = builder.create()

        alertDialog.setCancelable(true)
        alertDialog.show()
    }
}