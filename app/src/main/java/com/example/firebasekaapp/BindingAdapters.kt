package com.example.firebasekaapp

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.firebasekaapp.adapters.SearchAdapter
import com.example.firebasekaapp.models.User
import com.example.firebasekaapp.ui.search.UserStatus
import com.google.firebase.firestore.DocumentSnapshot


@BindingAdapter("imageUrl")
fun bindUserImage(imgView: ImageView,imgUrl: String?){
    imgUrl?.let {
        imgView.load(imgUrl){
            placeholder(R.drawable.loading_animation)
            error(R.drawable.ic_baseline_person_24)
        }
    }
}

@BindingAdapter("listData")
fun bindUserRecyclerView(recyclerView: RecyclerView,data: List<User>?){
    val adapter = recyclerView.adapter as SearchAdapter
    adapter.submitList(data)
}

@BindingAdapter("userStatus")
fun bindSearchStatus(statusImageView: ImageView,status: UserStatus){
    when(status){
        UserStatus.LOADING ->{
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
        UserStatus.ERROR ->{
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        UserStatus.DONE ->{
            statusImageView.visibility = View.GONE
        }
    }
}