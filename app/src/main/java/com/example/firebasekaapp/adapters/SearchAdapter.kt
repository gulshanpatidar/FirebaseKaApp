package com.example.firebasekaapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasekaapp.R
import com.example.firebasekaapp.daos.UserDao
import com.example.firebasekaapp.databinding.UserSearchItemLayoutBinding
import com.example.firebasekaapp.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SearchAdapter(): ListAdapter<User,SearchAdapter.ViewHolder?>(DiffCallback)
{
    private val currentUserId = Firebase.auth.currentUser!!.uid
    private val userDao = UserDao()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(UserSearchItemLayoutBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = getItem(position)
        holder.followButton.setOnClickListener {
            if (user.followers.contains(currentUserId)){
                userDao.removeFollower(user.userId,holder.followButton,null,null)
            }else{
                userDao.addFollower(user.userId,holder.followButton,null,null)
            }
        }
        holder.bind(user,currentUserId)
    }

    class ViewHolder(private var binding: UserSearchItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        val followButton: Button = binding.followButtonInSearch
        @SuppressLint("ResourceAsColor")
        fun bind(user: User, currentUserId: String){
            binding.user = user
            if (user.followers.contains(currentUserId)){
                binding.followButtonInSearch.text = "Following"
                binding.followButtonInSearch.setBackgroundColor(R.color.material_on_background_disabled)
            }else{
                binding.followButtonInSearch.text = "Follow"
            }
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<User>(){
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.userId == newItem.userId
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}