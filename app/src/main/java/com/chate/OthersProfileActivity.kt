package com.chate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class OthersProfileActivity : AppCompatActivity() {

    val mAuth = FirebaseAuth.getInstance()
    var accountEmailTextView: TextView? = null
    var accountNameTextView: TextView? = null
    var aboutTextView: TextView? = null
    var profileImage: CircleImageView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_others_profile)
        val profileToolbar: Toolbar = findViewById(R.id.otherUserToolbar)
        setSupportActionBar(profileToolbar)

        //Set up Account Information
        accountEmailTextView = findViewById(R.id.accountEmailTextView)

        accountNameTextView = findViewById(R.id.accountNameTextView)
        aboutTextView = findViewById(R.id.aboutTextView)
        profileImage = findViewById(R.id.acc_info_profile_image)

        val otherUserID = intent.getStringExtra("userID")

        FirebaseDatabase.getInstance().getReference().child("users").child(otherUserID!!).addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                accountEmailTextView?.text = snapshot.child("email").value.toString()
                accountNameTextView?.text = snapshot.child("name").value.toString()
                profileToolbar.title = snapshot.child("name").value.toString()
                if (snapshot.child("about").exists()){
                    aboutTextView?.text = snapshot.child("about").value.toString()
                }

                Picasso.get().load(snapshot.child("profileImageURL").value.toString()).placeholder(R.drawable.profile).into(profileImage)
            }
            override fun onCancelled(error: DatabaseError) { }
        })

    }


}