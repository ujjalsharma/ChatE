package com.chate

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.widget.EditText
import androidx.appcompat.widget.SearchView
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
    var searchView: SearchView? = null


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

        FirebaseDatabase.getInstance().getReference().child("users").orderByChild("name").addValueEventListener(object :
            ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                chooseUserList = ArrayList()


                for (userIDsnap in snapshot.children) {

                    val userid = userIDsnap.key.toString()
                    if (userid!=mAuth.currentUser?.uid.toString()){
                        (chooseUserList as ArrayList<String>).add(userid)
                    }

                }
                chooseUserAdapter = ChooseUserAdapter(this@ChooseUserActivity,
                    chooseUserList as MutableList<String>
                )
                chooseUserRecyclerView!!.adapter = chooseUserAdapter
            }


            override fun onCancelled(error: DatabaseError) {}
        })

    }

    @SuppressLint("ResourceAsColor")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_users_menu, menu)

        // Associate searchable configuration with the SearchView
        // Associate searchable configuration with the SearchView
        val searchManager: SearchManager =
            getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu?.findItem(R.id.search)?.actionView as SearchView
        val searchEditText: EditText = searchView!!.findViewById(R.id.search_src_text) as EditText


        searchEditText.setHint("Search")
        searchEditText.setHintTextColor(Color.parseColor("#929292"))
        searchEditText.setTextColor(Color.parseColor("#000000"))
        searchView!!.setSearchableInfo(
            searchManager.getSearchableInfo(componentName)
        )


        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.isNotBlank()){
                    filter(text!!)
                } else {
                    chooseUserAdapter!!.filterList(chooseUserList!! as ArrayList<String>)
                    chooseUserRecyclerView!!.scrollToPosition(0)
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        return true
    }

    fun filter(text: String?) {
        //new array list that will hold the filtered data
        var filterdNames: List<String>? = null
        filterdNames = ArrayList()

        //looping through existing elements
        for (s in chooseUserList!!) {
            //if the existing elements contains the search input
            var singleUserID: String? = null
            FirebaseDatabase.getInstance().getReference().child("users")
                .child(s.toString()).child("name").addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {}
                    override fun onDataChange(snapshot: DataSnapshot) {
                        singleUserID = snapshot.value.toString()
                        if (singleUserID!!.toLowerCase().contains(text!!.toLowerCase())) {
                            //adding the element to filtered list
                            (filterdNames as ArrayList<String>).add(s!!)
                        }
                    }

                })

        }

        chooseUserAdapter!!.filterList(filterdNames)

    }


    override fun onBackPressed() {

        if (!searchView!!.isIconified()) {
            chooseUserAdapter!!.filterList(chooseUserList!! as ArrayList<String>)
            chooseUserRecyclerView!!.scrollToPosition(0)
            searchView!!.setIconified(true);
            searchView!!.setIconified(true);
        } else {
            super.onBackPressed()
        }

    }



}