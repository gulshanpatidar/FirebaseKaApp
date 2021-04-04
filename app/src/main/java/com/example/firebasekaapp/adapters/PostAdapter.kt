package com.example.firebasekaapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebasekaapp.R
import com.example.firebasekaapp.Utils
import com.example.firebasekaapp.Utils1
import com.example.firebasekaapp.models.Post
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PostAdapter(options: FirestoreRecyclerOptions<Post>,private val listener: IPostAdapter):
    FirestoreRecyclerAdapter<Post,PostAdapter.PostViewHolder>(options) {


    class PostViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val postText: TextView = itemView.findViewById(R.id.post_title_in_post)
        val postImage: ImageView = itemView.findViewById(R.id.post_image_in_post)
        val userName: TextView = itemView.findViewById(R.id.user_name_in_post)
        val createdAt: TextView = itemView.findViewById(R.id.created_at_in_post)
        val likeCount: TextView = itemView.findViewById(R.id.like_count_in_post)
        val userImage: ImageView = itemView.findViewById(R.id.user_image_in_Post)
        val likeButton: ImageView = itemView.findViewById(R.id.like_button_in_post)
        val commentButton: ImageView = itemView.findViewById(R.id.comment_button_in_post)
        val commentText: EditText = itemView.findViewById(R.id.comment_text_in_post)
        val commentOutButton: Button = itemView.findViewById(R.id.comment_out_button_in_post)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val viewHolder = PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post,parent,false))
        viewHolder.likeButton.setOnClickListener {
            //call the onLikeClicked from here and pass the post id from the snapshots
            listener.onLikeClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }
        viewHolder.commentButton.setOnClickListener{
            listener.onCommentClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id,viewHolder.commentText.text.toString())
        }
        return viewHolder
    }

    //this will bind all the views in our layout with the data
    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {
        holder.postText.text = model.text
        //set the post image
//        holder.postImage.setImageURI(model.imageUrl)
        Glide.with(holder.postImage.context).load(model.imageUrl).into(holder.postImage)
        holder.userName.text = model.createdBy.username
        //set the placeholder image for now
        holder.userImage.setImageResource(R.drawable.ic_baseline_person_24)
        holder.likeCount.text = model.likedBy.size.toString()
        holder.createdAt.text = Utils1.getTimeAgo(model.createdAt)

        //get the instance of the auth
        val auth = Firebase.auth
        val currentUserId = auth.currentUser!!.uid
        val isLiked = model.likedBy.contains(currentUserId)
        if (isLiked){
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context,R.drawable.ic_liked_icon))
        }else{
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context,R.drawable.ic_unliked_icon))
        }
    }
}

//this interface is used to handle the click listener to the like button by taking the post id as argument
interface IPostAdapter{
    fun onLikeClicked(postId: String)
    fun onCommentClicked(postId: String,text: String)
}