package com.chate

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.ArrayList

class ChatItemAdapter(
    var mContext: Context,
    var mData: List<ChatItem>
) : RecyclerView.Adapter<ChatItemAdapter.MyViewHolder>() {

    val mAuth = FirebaseAuth.getInstance()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val row =
            LayoutInflater.from(mContext).inflate(R.layout.row_chat_item, parent, false)
        return MyViewHolder(row)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val chatID = mData[position].chatID
        var userID: String? = null
        val ownID = mAuth.currentUser?.uid.toString()

        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.currentUser?.uid.toString())
            .child("chats").child(chatID!!).child("userID")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    userID = snapshot.value.toString()

                    FirebaseDatabase.getInstance().getReference().child("users").child(userID!!)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {}

                            override fun onDataChange(snapshot: DataSnapshot) {

                                holder.usernamaeChatItemTV.text = snapshot.child("name").value.toString()
                                Picasso.get().load(snapshot.child("profileImageURL").value.toString()).placeholder(R.drawable.profile).into(holder.profileChatItemImage)

                            }

                        })

                    FirebaseDatabase.getInstance().getReference().child("chats").child(chatID)
                        .orderByChild("timestamp").limitToLast(1)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {}

                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                for (msnapshot in dataSnapshot.children){
                                    holder.timeChatItemTV.text = messagedate(msnapshot.child("timestamp").value.toString())


                                    var message =  msnapshot.child("message").value.toString()

                                    if (msnapshot.child("userID").value.toString()==ownID){
                                        holder.messageChatItemTV.text ="~ " + message

                                    } else {
                                        if (msnapshot.child("read").value==true){
                                            holder.messageChatItemTV.text = message
                                        } else {
                                            holder.messageChatItemTV.setTypeface(holder.messageChatItemTV.getTypeface(), Typeface.BOLD)
                                            holder.messageChatItemTV.text = message
                                        }
                                        holder.messageChatItemTV.text = message

                                    }


                                }

                            }

                        })

                    FirebaseDatabase.getInstance().getReference().child("chats").child(chatID)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {}

                            override fun onDataChange(snapshot: DataSnapshot) {

                                var unreadCount = 0

                                for (snap in snapshot.children){
                                    if (snap.child("userID").value ==userID && !snap.child("read").exists()){
                                        unreadCount += 1
                                    }
                                }

                                if (unreadCount>0){
                                    holder.unreadCountChatItemTV.setVisibility(View.VISIBLE);
                                    holder.unreadCountChatItemTV.text = "$unreadCount"
                                    holder.timeChatItemTV.setTextColor(Color.parseColor("#03A9F4"))
                                }

                            }

                        })


                }

                override fun onCancelled(error: DatabaseError) {}

            })









        holder.itemView.setOnClickListener {

            val intent = Intent(mContext, ChatActivity::class.java)
            intent.putExtra("chatID", chatID)
            intent.putExtra("userID", userID)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            mContext.startActivity(intent)

        }

    }

    override fun getItemCount(): Int {
        return mData.size
    }

    inner class MyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var usernamaeChatItemTV: TextView
        var timeChatItemTV: TextView
        var messageChatItemTV: TextView
        var unreadCountChatItemTV: TextView
        var profileChatItemImage: CircleImageView

        init {
            usernamaeChatItemTV = itemView.findViewById(R.id.chat_item_name)
            messageChatItemTV = itemView.findViewById(R.id.chat_message_item)
            unreadCountChatItemTV = itemView.findViewById(R.id.unread_tv)
            timeChatItemTV = itemView.findViewById(R.id.chat_message_item_time)
            profileChatItemImage = itemView.findViewById(R.id.chat_item_profile_image)

        }
    }

    fun messagetime(time: String?): String? {

        val dateFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val pasTime = dateFormat.parse(time)

        val curFormater = SimpleDateFormat("HH:mm")
        val newDateStr = curFormater.format(pasTime)
        return newDateStr
    }

    fun messagedate(time: String?): String? {

        var covTimess = ""


        val dateFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val pasTime = dateFormat.parse(time)

        val curFormater = SimpleDateFormat("dd MMMM yyyy")
        val newDateStr = curFormater.format(pasTime)

        if (isYesterday(pasTime.time)){
            covTimess = "Yesterday"
        } else if (isToday(pasTime.time)){
            covTimess = messagetime(time)!!
        } else {
            covTimess = newDateStr
        }

        return covTimess
    }

    fun isYesterday(whenInMillis: Long): Boolean {
        return DateUtils.isToday(whenInMillis + DateUtils.DAY_IN_MILLIS)
    }

    fun isToday(whenInMillis: Long): Boolean {
        return DateUtils.isToday(whenInMillis)
    }

    fun filterList(filterdNames: ArrayList<ChatItem>) {
        this.mData = filterdNames
        notifyDataSetChanged()
    }



}