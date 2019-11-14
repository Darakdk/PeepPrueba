package com.testeando.pruebapeep

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_home.*
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.widget.ImageView
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream


class HomeActivity : AppCompatActivity() {

    lateinit var database: DatabaseReference
    lateinit var userList: MutableList<User>
    lateinit var listView: ListView
    private lateinit var auth: FirebaseAuth
    lateinit var storage: FirebaseStorage


    //Download Image from a link
    private inner class DownloadImageTask(internal var bmImage: ImageView) :
        AsyncTask<String, Void, Bitmap>() {

        override fun doInBackground(vararg urls: String): Bitmap? {
            val urldisplay = urls[0]
            var mIcon11: Bitmap? = null
            try {
                val `in` = java.net.URL(urldisplay).openStream()
                mIcon11 = BitmapFactory.decodeStream(`in`)
            } catch (e: Exception) {
                Log.e("Error", e.message)
                e.printStackTrace()
            }

            return mIcon11
        }

        override fun onPostExecute(result: Bitmap) {
            bmImage.setImageBitmap(result)
            uploadImage()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        userList = mutableListOf()
        listView = userListView
        storage = FirebaseStorage.getInstance()

        signOut.setOnClickListener{ signOut() }

        database.child("User").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()) {
                    userList.clear()
                    for (user in dataSnapshot.children) {
                        val u = user.getValue(User::class.java)
                        userList.add(u!!)
                    }

                    val adapter = ListAdapter(applicationContext, userList)
                    listView.adapter = adapter

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        })
        val imgLink=auth.currentUser?.photoUrl.toString()
        DownloadImageTask(profImg)
            .execute(imgLink);






    }

    fun signOut(){
        FirebaseAuth.getInstance().signOut()
        finish()
    }

    fun uploadImage()
    {
        val storageRef = storage.reference
        var bitMp=profImg.drawable.toBitmap()
        bitMp= Bitmap.createScaledBitmap(bitMp,400,400,false)
        val outStream = ByteArrayOutputStream()
        bitMp.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
        val data = outStream.toByteArray()

        val profileImageRef = storageRef.child("UserPhotos").child(auth.currentUser?.uid!!).child("userImage.jpg")

        var uploadTask = profileImageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener {
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
        }
    }




}
