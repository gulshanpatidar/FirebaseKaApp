package com.example.firebasekaapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.firebasekaapp.R
import com.example.firebasekaapp.Utils1
import com.example.firebasekaapp.models.Message
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MessageAdapter(val messages: List<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val currentUserId = Firebase.auth.currentUser!!.uid
    private val VIEW_TYPE_MESSAGE_SENT = 0
    private val VIEW_TYPE_MESSAGE_RECEIVED = 1

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        if (message.createdBy == currentUserId) {
            return VIEW_TYPE_MESSAGE_SENT
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED
        }
    }

    class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.text_message_message_me)
        val messageTime: TextView = itemView.findViewById(R.id.text_timestamp_message_me)

        fun bind(message: Message) {
            messageText.text = message.text
            messageTime.text = Utils1.getTimeAgo(message.createdAt)
        }
    }

    class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.text_message_message_other)
        val messageTime: TextView = itemView.findViewById(R.id.text_timestamp_message_other)
        val userName: TextView = itemView.findViewById(R.id.user_name_message_other)
        val userPhoto: ImageView = itemView.findViewById(R.id.user_image_message_other)

        fun bind(message: Message) {
            messageText.text = message.text
            messageTime.text = Utils1.getTimeAgo(message.createdAt)
            userPhoto.load(message.userImage) {
                transformations(CircleCropTransformation())
                placeholder(R.drawable.loading_animation)
                error(R.drawable.ic_baseline_person_24)
            }
            userName.text = message.userName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            return SentMessageViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_message_me, parent, false)
            )
        } else {
            return ReceivedMessageViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_other, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType){
            VIEW_TYPE_MESSAGE_SENT ->{
                (holder as SentMessageViewHolder).bind(messages[position])
            }
            VIEW_TYPE_MESSAGE_RECEIVED->{
                (holder as ReceivedMessageViewHolder).bind(messages[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}