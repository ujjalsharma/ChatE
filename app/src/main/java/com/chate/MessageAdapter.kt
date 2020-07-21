package com.chate

import android.content.Context
import android.text.format.DateUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat

class MessageAdapter(
    var mContext: Context,
    var mData: List<Message>
) : RecyclerView.Adapter<MessageAdapter.MyViewHolder>() {

    val mAuth = FirebaseAuth.getInstance()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val row =
            LayoutInflater.from(mContext).inflate(R.layout.message_item, parent, false)
        return MyViewHolder(row)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.timestampTV.text = messagetime(mData[position].timestamp)

        holder.messageItemTV.text = mData[position].message


        if (position==0){

            holder.dateMessageButton.text = messagedate(mData[position].timestamp)

        } else if (position>0) {

            if (messagedate(mData[position].timestamp)!=messagedate(mData[position-1].timestamp)){
                holder.dateMessageButton.text = messagedate(mData[position].timestamp)
            } else {
                holder.dateMessageButton.visibility = View.GONE
            }

        }




        if (mData[position].userID!=mAuth.currentUser?.uid.toString()){

            holder.messageRL.setPadding(20, 20,80,20)
            holder.messageItemLL.background = mContext.resources.getDrawable(R.drawable.box_msg_from)
            FirebaseDatabase.getInstance().getReference().child("chats")
                .child(mData[position].chatID!!).child(mData[position].messageID!!)
                .child("read").setValue(true)

        } else if (mData[position].userID==mAuth.currentUser?.uid.toString()) {
            holder.messageRL.gravity = Gravity.RIGHT
            holder.messageRL.setPadding(80, 20,20,20)
            holder.messageItemLL.background = mContext.resources.getDrawable(R.drawable.box_msg_you)
        }


    }

    override fun getItemCount(): Int {
        return mData.size
    }

    inner class MyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        var timestampTV: TextView
        var messageItemTV: TextView
        var messageRL: RelativeLayout
        var messageItemLL: LinearLayout
        var dateMessageButton: TextView

        init {
            messageRL = itemView.findViewById(R.id.messageCLayout)
            messageItemTV = itemView.findViewById(R.id.messageItemTV)
            timestampTV = itemView.findViewById(R.id.msgTimeTV)
            messageItemLL = itemView.findViewById(R.id.messageItem)
            dateMessageButton = itemView.findViewById(R.id.dateMessageItem)

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
            covTimess = "Today"
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


}