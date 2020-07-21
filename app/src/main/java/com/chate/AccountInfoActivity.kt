package com.chate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AccountInfoActivity : AppCompatActivity() {

    val mAuth = FirebaseAuth.getInstance()
    var accountEmailTextView: TextView? = null
    var accountNameTextView: TextView? = null
    var aboutTextView: TextView? = null
    var profileImage: CircleImageView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_info)

        //Set up Account Information
        accountEmailTextView = findViewById(R.id.accountEmailTextView)
        accountEmailTextView?.text = mAuth.currentUser?.email.toString()
        accountNameTextView = findViewById(R.id.accountNameTextView)
        aboutTextView = findViewById(R.id.aboutTextView)
        profileImage = findViewById(R.id.acc_info_profile_image)

        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.currentUser?.uid.toString()).addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                accountNameTextView?.text = snapshot.child("name").value.toString()
                if (snapshot.child("about").exists()){
                    aboutTextView?.text = snapshot.child("about").value.toString()
                }

                Picasso.get().load(snapshot.child("profileImageURL").value.toString()).placeholder(R.drawable.profile).into(profileImage)
            }
            override fun onCancelled(error: DatabaseError) { }
        })




    }

    override fun onBackPressed() {
        val intent = Intent(this, ChatListActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    fun updateInfoClicked(view: View){
        val intent = Intent(this, UpdateAccountInfoActivity::class.java)
        startActivity(intent)
    }

}