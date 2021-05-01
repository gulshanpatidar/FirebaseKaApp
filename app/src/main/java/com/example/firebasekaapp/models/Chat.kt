package com.example.firebasekaapp.models

//data class Chat(val messages: ArrayList<Message> = ArrayList(),val userId: String = "",val userName: String = "",val userImage: String? = "")
data class Chat(val messages: ArrayList<Message> = ArrayList(),val user1Id: String = "",val user1Name: String = "",val user1Image: String? = "",val user2Id: String = "",val user2Name: String = "",val user2Image: String? = "")

//user ke andar chat ka ek instance banayenge or us hisaab se jab user messages section me jaayega to check karenge ke pass koi chat he ya nahi or agar koi chat he to fir kiske saath he or uske baad se hi saari details ko hum fetch kar paayenge.
//or nayi chat start karne ke liye jab user kisi dusre user ki profile me jaake message pe click karega tab target user ki id ko hum store kar lenge or current user ki id ki help lekar hum ek nayi chat start kar sakte he or fir usi hisaab se hum aage ke operations bhi perform karenge.

//isme ek bahut hi badi samasya aa rahi he. chat ek user ke liye esi he to fir vo dusre user ke liye vaisi he. or iski vajah se iske data structure ko change karne ki jarurat pad rahi he.