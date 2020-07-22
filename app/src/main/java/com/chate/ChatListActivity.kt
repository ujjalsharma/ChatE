package com.chate

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.ArrayList

class ChatListActivity : AppCompatActivity() {

    var chatListRV: RecyclerView? = null
    var chatsList: List<ChatItem>? = null
    val mAuth = FirebaseAuth.getInstance()
    var chatAdapter: ChatItemAdapter? = null
    var chatListToolbar: Toolbar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)
        chatListToolbar = findViewById(R.id.chatListToolbar)
        setSupportActionBar(chatListToolbar)

        chatListRV = findViewById(R.id.chatlistRV)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        chatListRV?.setLayoutManager(layoutManager)
        chatListRV?.setHasFixedSize(true)
        chatListRV?.setItemViewCacheSize(50)


    }

    override fun onStart() {
        super.onStart()


        FirebaseDatabase.getInstance().getReference().child("users")
            .child(mAuth.currentUser?.uid.toString()).child("chats")
            .addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                for (chatItemsnap in snapshot.children) {
                    val chatItemID = chatItemsnap.child("chatID").value.toString()

                    FirebaseDatabase.getInstance().getReference().child("chats").child(chatItemID)
                        .orderByChild("timestamp").limitToLast(1)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {}

                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (msnapshot in snapshot.children) {
                                    val latestTimeStampString = msnapshot.child("timestamp").value.toString()
                                    FirebaseDatabase.getInstance().getReference().child("users")
                                        .child(mAuth.currentUser?.uid.toString()).child("chats")
                                        .child(chatItemID).child("latestTimestamp")
                                        .setValue(latestTimeStampString)
                                }
                            }

                        })
                }

            }

            override fun onCancelled(error: DatabaseError) {}

        })


        FirebaseDatabase.getInstance().getReference().child("users")
            .child(mAuth.currentUser?.uid.toString()).child("chats")
            .orderByChild("latestTimestamp").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}

                override fun onDataChange(snapshot: DataSnapshot) {


                    chatsList = ArrayList()

                    for (snap in snapshot.children) {
                        val chatitem: ChatItem? = snap.getValue(ChatItem::class.java)
                        (chatsList as ArrayList<ChatItem>).add(chatitem!!)
                    }

                    if (chatsList!!.isNotEmpty()){
                        chatAdapter = ChatItemAdapter(this@ChatListActivity,
                            chatsList as MutableList<ChatItem>
                        )
                        chatListRV!!.adapter = chatAdapter

                    }


                }

            })







    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_feed, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item?.itemId == R.id.logout){
            mAuth.signOut()
            finish()
        } else if (item?.itemId == R.id.my_profile) {
            val intent = Intent(this, AccountInfoActivity::class.java)
            startActivity(intent)
        } else if (item?.itemId == R.id.newchat){
            val intent = Intent(this, ChooseUserActivity::class.java)
            startActivity(intent)
        }


        return super.onOptionsItemSelected(item)
    }

    fun newChatClicked(view: View) {
        val intent = Intent(this, ChooseUserActivity::class.java)
        startActivity(intent)
    }



    override fun onBackPressed() {
        finishAffinity()
    }

}