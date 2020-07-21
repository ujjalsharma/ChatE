package com.chate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

class ChooseUserActivity : AppCompatActivity() {

    val mAuth = FirebaseAuth.getInstance()
    var chooseUserRecyclerView: RecyclerView? = null
    var chooseUserAdapter: ChooseUserAdapter? = null
    var chooseUserList: List<String>? = null
    var chooseUserToolbar: androidx.appcompat.widget.Toolbar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_user)

        chooseUserToolbar = findViewById(R.id.likesToolbar)
        setSupportActionBar(chooseUserToolbar)




        chooseUserRecyclerView = findViewById(R.id.chooseUserRV)
        chooseUserRecyclerView?.setLayoutManager(LinearLayoutManager(this))
        chooseUserRecyclerView?.setHasFixedSize(true)
        chooseUserRecyclerView?.setItemViewCacheSize(50)



    }

    override fun onStart() {
        super.onStart()

        FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(object :
            ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                chooseUserList = ArrayList()


                for (userIDsnap in snapshot.children) {

                    val userid = userIDsnap.key.toString()
                    if (userid!=mAuth.currentUser?.uid.toString()){
                        (chooseUserList as ArrayList<String>).add(userid)
                    }

                }
                chooseUserList = (chooseUserList as ArrayList<String>).reversed().toMutableList()
                chooseUserAdapter = ChooseUserAdapter(this@ChooseUserActivity,
                    chooseUserList as MutableList<String>
                )
                chooseUserRecyclerView!!.adapter = chooseUserAdapter
            }


            override fun onCancelled(error: DatabaseError) {}
        })

    }

}