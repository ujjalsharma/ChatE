package com.chate

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

class ChooseUserAdapter(
    var mContext: Context,
    var mData: List<String>
) : RecyclerView.Adapter<ChooseUserAdapter.MyViewHolder>() {

    val mAuth = FirebaseAuth.getInstance()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val row =
            LayoutInflater.from(mContext).inflate(R.layout.row_choose_user_item, parent, false)
        return MyViewHolder(row)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        FirebaseDatabase.getInstance().getReference().child("users").child(mData[position]).addValueEventListener(object :
            ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                holder.nameChooseUserTextView.text = snapshot.child("name").value.toString()

                if (snapshot.child("about").exists()){
                    val about = snapshot.child("about").value.toString()
                    holder.aboutUserTextView.text = about


                }

                Picasso.get().load(snapshot.child("profileImageURL").value.toString()).placeholder(R.drawable.profile).into(holder.profileChooseUserImage)
            }

            override fun onCancelled(error: DatabaseError) {}
        })


        holder.itemView.setOnClickListener {
            val otherID = mData[position]


            FirebaseDatabase.getInstance().getReference().child("users")
                .child(mAuth.currentUser?.uid.toString()).child("chatUsers").child(otherID).addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {}

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()){
                            val chatID = snapshot.value.toString()
                            val intent = Intent(mContext, ChatActivity::class.java)
                            intent.putExtra("chatID", chatID)
                            intent.putExtra("userID", otherID)
                            mContext.startActivity(intent)
                        } else {

                            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                            val currentDate = sdf.format(Date())
                            val chatID = "chat" + UUID.randomUUID().toString() + currentDate

                            FirebaseDatabase.getInstance().getReference().child("users")
                                .child(mAuth.currentUser?.uid.toString()).child("chatUsers")
                                .child(otherID!!).setValue(chatID!!)
                            FirebaseDatabase.getInstance().getReference().child("users")
                                .child(otherID!!).child("chatUsers")
                                .child(mAuth.currentUser?.uid.toString()).setValue(chatID!!)

                            val intent = Intent(mContext, ChatActivity::class.java)
                            intent.putExtra("chatID", chatID)
                            intent.putExtra("userID", otherID)
                            mContext.startActivity(intent)

                        }
                    }

                })

        }




    }

    override fun getItemCount(): Int {
        return mData.size
    }

    inner class MyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var nameChooseUserTextView: TextView
        var aboutUserTextView: TextView
        var profileChooseUserImage: CircleImageView

        init {
            nameChooseUserTextView = itemView.findViewById(R.id.chooseUserNameTV)
            aboutUserTextView = itemView.findViewById(R.id.aboutUserTV)
            profileChooseUserImage = itemView.findViewById(R.id.choose_user_profile_image)

        }
    }

    fun filterList(filterdNames: ArrayList<String>) {
        this.mData = filterdNames
        notifyDataSetChanged()
    }


}