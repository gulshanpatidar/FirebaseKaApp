package com.example.firebasekaapp.adapters

import android.graphics.drawable.AnimatedVectorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import coil.load
import coil.transform.CircleCropTransformation
import com.example.firebasekaapp.R
import com.example.firebasekaapp.Utils1
import com.example.firebasekaapp.daos.UserDao
import com.example.firebasekaapp.models.Post
import com.example.firebasekaapp.models.User
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import xyz.hanks.library.bang.SmallBangView;

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
        val commentLinearLayout: View = itemView.findViewById(R.id.comment_linear_layout)
        val viewAllComment: TextView = itemView.findViewById(R.id.view_all_comments_in_post)
        val likeButtonAnimation: SmallBangView = itemView.findViewById(R.id.like_button_animation_in_post)
        val bigLikeButtonInPost: ImageView = itemView.findViewById(R.id.big_like_button_in_post)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val viewHolder = PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post,parent,false))

        var isDoubleClicked = false

        viewHolder.likeButton.setOnClickListener {
            isDoubleClicked = false
            //call the onLikeClicked from here and pass the post id from the snapshots
            listener.onLikeClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id,isDoubleClicked)
                viewHolder.likeButtonAnimation.likeAnimation()
//                viewHolder.bigLikeButtonAnimation.visibility = View.VISIBLE
//                viewHolder.bigLikeButtonAnimation.likeAnimation()
        }

        val drawable = viewHolder.bigLikeButtonInPost.drawable

        viewHolder.postImage.setOnClickListener(object :DoubleClickListener(){
            override fun onDoubleClick(v: View?) {
                isDoubleClicked = true
                listener.onLikeClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id,isDoubleClicked)
                viewHolder.bigLikeButtonInPost.alpha = 1f
                if (drawable is AnimatedVectorDrawableCompat){
                    val avd = drawable
                    avd.start()
                } else if (drawable is AnimatedVectorDrawable){
                    val avd2 = drawable
                    avd2.start()
                }
//
            }
        })

        viewHolder.userImage.setOnClickListener {
            listener.onUserClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }

        viewHolder.userName.setOnClickListener {
            listener.onUserClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }

        viewHolder.commentButton.setOnClickListener{
            //show the comment edit text and comment button
            if (viewHolder.commentLinearLayout.visibility == View.GONE){
                viewHolder.commentLinearLayout.visibility = View.VISIBLE
            }
            else if (viewHolder.commentLinearLayout.visibility == View.VISIBLE){
                viewHolder.commentLinearLayout.visibility = View.GONE
            }
        }

        viewHolder.commentOutButton.setOnClickListener {
            //comment out the post
            listener.onCommentClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id,viewHolder.commentText.text.toString())
            viewHolder.commentLinearLayout.visibility = View.GONE
        }

        viewHolder.viewAllComment.setOnClickListener {
            listener.onViewCommentClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }

        return viewHolder
    }

    //this will bind all the views in our layout with the data
    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {
        holder.postText.text = model.text
        //set the post image
//        holder.postImage.setImageURI(model.imageUrl)
//        Glide.with(holder.postImage.context).load(model.imageUrl).into(holder.postImage)
        holder.postImage.load(model.imageUrl){
            placeholder(R.drawable.loading_animation)
        }
        holder.userName.text = model.createdBy.username
        //set the placeholder image for now
        holder.userImage.setImageResource(R.drawable.ic_baseline_person_24)
        holder.likeCount.text = model.likedBy.size.toString()
        holder.createdAt.text = Utils1.getTimeAgo(model.createdAt)

        val userId = model.createdBy.userId
        val userDao = UserDao()

        GlobalScope.launch {
            val user = userDao.getUserById(userId).await().toObject(User::class.java)!!
            withContext(Dispatchers.Main){
                holder.userImage.load(user.userImage){
                    transformations(CircleCropTransformation())
                    placeholder(R.drawable.loading_animation)
                    error(R.drawable.ic_baseline_person_24)
                }
            }
        }

        //get the instance of the auth
        val auth = Firebase.auth
        val currentUserId = auth.currentUser!!.uid
        val isLiked = model.likedBy.contains(currentUserId)
        if (isLiked){
            holder.likeButtonAnimation.isSelected = true
        }else{
            holder.likeButtonAnimation.isSelected = false
        }
    }
}

//this interface is used to handle the click listener to the like button by taking the post id as argument
interface IPostAdapter{
    fun onLikeClicked(postId: String,isDoubleClicked: Boolean)
    fun onCommentClicked(postId: String,text: String)
    fun onViewCommentClicked(postId: String)
    fun onImageDoubleClicked()
    fun onUserClicked(postId: String)
}

abstract class DoubleClickListener : View.OnClickListener {
    var lastClickTime: Long = 0
    override fun onClick(v: View?) {
        val clickTime = System.currentTimeMillis()
        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
            onDoubleClick(v)
        }
        lastClickTime = clickTime
    }

    abstract fun onDoubleClick(v: View?)

    companion object {
        private const val DOUBLE_CLICK_TIME_DELTA: Long = 300 //milliseconds
    }
}