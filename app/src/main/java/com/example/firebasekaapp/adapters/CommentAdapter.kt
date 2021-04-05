package com.example.firebasekaapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasekaapp.R
import com.example.firebasekaapp.Utils1
import com.example.firebasekaapp.models.Comment
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class CommentAdapter(val context: Context,val comments: List<Comment>,val postId: String): RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val commentText: TextView = itemView.findViewById(R.id.comment_text_in_comment)
        val userImage: ImageView = itemView.findViewById(R.id.user_image_in_comment)
        val userName: TextView = itemView.findViewById(R.id.user_name_in_comment)
        val createdAt: TextView = itemView.findViewById(R.id.created_at_in_comment)
        val likeButton: ImageButton = itemView.findViewById(R.id.like_button_in_comment)
        val replyButton: TextView = itemView.findViewById(R.id.reply_button_in_comment)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentViewHolder {
        val viewHolder = CommentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_comment,parent,false))

        viewHolder.likeButton.setOnClickListener{
            //handle the comment like action
        }

        viewHolder.replyButton.setOnClickListener{
            //handle the comment reply action
        }

        viewHolder.userImage.setOnClickListener{
            //go to the user profile from here
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val user = Firebase.auth.currentUser
        val comment = comments[position]
        holder.commentText.text = comment.text
        holder.userName.text = user.displayName
        holder.createdAt.text = Utils1.getTimeAgo(System.currentTimeMillis())
    }

    override fun getItemCount(): Int {
        return comments.size
    }
}
