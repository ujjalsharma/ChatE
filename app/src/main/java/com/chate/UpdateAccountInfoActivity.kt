package com.chate

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class UpdateAccountInfoActivity : AppCompatActivity() {

    val mAuth = FirebaseAuth.getInstance()
    var accountNameEditText: EditText? = null
    var aboutEditText: EditText? = null
    var profileImageView: ImageView? = null
    var imageName = mAuth.currentUser?.uid.toString() + ".jpg"
    var profileImageImageChangedFlag = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_account_info)

        profileImageView = findViewById(R.id.profileImageView)

        profileImageImageChangedFlag = 0

        accountNameEditText = findViewById(R.id.accountNameEditText)
        aboutEditText = findViewById(R.id.aboutEditText)


        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.currentUser?.uid.toString()).addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                accountNameEditText?.setText(snapshot.child("name").value.toString())

                if (snapshot.child("about").exists()){
                    aboutEditText?.setText(snapshot.child("about").value.toString())
                }
                Picasso.get().load(snapshot.child("profileImageURL").value.toString()).placeholder(R.drawable.profile).into(profileImageView)
            }
            override fun onCancelled(error: DatabaseError) { }
        })

    }

    fun getPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun uploadImageClicked(view: View){
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else {
            getPhoto()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val selectedImage = data!!.data

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                profileImageImageChangedFlag = 1
                profileImageView?.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto()
            }
        }
    }

    fun saveClicked(view: View){

        if (accountNameEditText?.text.toString().isNotBlank()) {

            if (accountNameEditText?.text.toString().length < 70){
                FirebaseDatabase.getInstance().getReference().child("users")
                    .child(mAuth.currentUser?.uid.toString()).child("name")
                    .setValue(accountNameEditText?.text.toString())
            } else {
                Toast.makeText(this, "Exceeded character limit!", Toast.LENGTH_SHORT).show()
            }


        }

        if (aboutEditText?.text.toString().isNotBlank()) {


            FirebaseDatabase.getInstance().getReference().child("users")
                .child(mAuth.currentUser?.uid.toString()).child("about")
                .setValue(aboutEditText?.text.toString())

        }


        if (profileImageImageChangedFlag==1) {
            // Get the data from an ImageView as bytes
            profileImageView?.isDrawingCacheEnabled = true
            profileImageView?.buildDrawingCache()
            val bitmap = (profileImageView?.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()


            var uploadTask = FirebaseStorage.getInstance().getReference().child("profileImages")
                .child(imageName!!).putBytes(data)
            uploadTask.addOnFailureListener {
                // Handle unsuccessful uploads
                Toast.makeText(this, "Upload Failed!", Toast.LENGTH_SHORT).show()
            }.addOnSuccessListener { taskSnapshot ->
                // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                // ...
                FirebaseStorage.getInstance().getReference().child("profileImages")
                    .child(imageName!!).downloadUrl.addOnSuccessListener {

                        val downloadURL = it

                        FirebaseDatabase.getInstance().getReference().child("users")
                            .child(mAuth.currentUser?.uid.toString()).child("profileImageURL")
                            .setValue(downloadURL.toString())


                        val intent = Intent(this, AccountInfoActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)

                    }.addOnFailureListener {

                    }


            }
        } else {
            val intent = Intent(this, AccountInfoActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)

        }


    }

    override fun onBackPressed() {
        val intent = Intent(this, AccountInfoActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    fun removePicClicked(view: View){

        FirebaseStorage.getInstance().getReference().child("profileImages").child(mAuth.currentUser?.uid.toString() + ".jpg").delete()
        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.currentUser?.uid.toString()).child("profileImageURL").removeValue()

        val intent = Intent(this, AccountInfoActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)

    }

    fun exitButtonClicked(view: View){
        val intent = Intent(this, AccountInfoActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }


}