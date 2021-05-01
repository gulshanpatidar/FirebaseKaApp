package com.example.firebasekaapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.firebasekaapp.R
import com.example.firebasekaapp.models.Chat
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class ChatAdapter(val chats: List<Chat>,private val listener: IChatAdapter):
RecyclerView.Adapter<ChatAdapter.ChatViewHolder>(){

    class ChatViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val userName: TextView = itemView.findViewById(R.id.user_name_in_chat)
        val userImage: ImageView = itemView.findViewById(R.id.user_image_in_chat)
        val lastMessage: TextView = itemView.findViewById(R.id.last_message_in_chat)
        val chatContainer: View = itemView.findViewById(R.id.container_chat)

        fun bind(chat: Chat){
            userName.text = chat.user2Name
            userImage.load(chat.user2Image){
                placeholder(R.drawable.loading_animation)
                error(R.drawable.ic_baseline_person_24)
                transformations(CircleCropTransformation())
            }
            val messages = chat.messages
            if(messages.size>=1){
                lastMessage.text = messages[messages.size-1].text
            }else{
                lastMessage.text = ""
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val viewHolder =  ChatViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_chat,parent,false))

        return viewHolder
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val n = chats.size - 1
        val chat = chats[n - position]
        holder.bind(chat)
        holder.chatContainer.setOnClickListener {
            listener.onChatClicked(chat)
        }
    }

    override fun getItemCount(): Int {
        return chats.size
    }
}

interface IChatAdapter{
    fun onChatClicked(chat: Chat)
}
