package com.chate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    var emailEditText: TextInputEditText? = null
    var passEditText: TextInputEditText? = null
    var nameEditText: TextInputEditText? = null
    val mAuth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailEditText = findViewById(R.id.emailEditText);
        passEditText = findViewById(R.id.passwordEditText);
        nameEditText = findViewById(R.id.nameEditText)

        if (mAuth.currentUser!=null){
            logIn()
        }

    }

    fun loginClicked(view: View){
        var email = emailEditText?.text.toString()

        // check if we can login the user
        mAuth.signInWithEmailAndPassword(
                emailEditText?.text.toString(),
                passEditText?.text.toString()
        )
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        logIn()
                    } else {
                        //sinup the user
                        mAuth.createUserWithEmailAndPassword(
                                emailEditText?.text.toString(),
                                passEditText?.text.toString()
                        ).addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Add to database
                                FirebaseDatabase.getInstance().getReference().child("users")
                                        .child(task.result?.user?.uid.toString()).child("email")
                                        .setValue(emailEditText?.text.toString())
                                FirebaseDatabase.getInstance().getReference().child("users")
                                        .child(task.result?.user?.uid.toString()).child("name")
                                        .setValue(nameEditText?.text.toString())
                                FirebaseDatabase.getInstance().getReference().child("users")
                                    .child(task.result?.user?.uid.toString()).child("about")
                                    .setValue("Hola, I am using ChatE!")
                                logIn()
                            } else {
                                Toast.makeText(this, "Login Failed! Try Again!", Toast.LENGTH_SHORT)
                                        .show()
                            }
                        }
                    }

                }

    }

    fun logIn(){

        val intent = Intent(this, ChatListActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)

    }
}