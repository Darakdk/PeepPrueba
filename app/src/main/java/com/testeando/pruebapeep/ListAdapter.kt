package com.testeando.pruebapeep

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.drawToBitmap
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.list_item.view.*
import java.io.File
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.widget.ImageView


class ListAdapter(val cntx: Context, val userList: List<User>) : ArrayAdapter<User>(cntx, R.layout.list_item,userList){


    lateinit var storage: FirebaseStorage

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater :  LayoutInflater = LayoutInflater.from(cntx)
        val row: View = layoutInflater.inflate(R.layout.list_item, null)

        val user = userList[position]
        storage = FirebaseStorage.getInstance()

        row.name.text=user.username
        val storageRef = storage.reference
        val pathReference = storageRef.child("UserPhotos").child(user.uid).child("userImage.jpg")

        val localFile = File.createTempFile("images"+user.uid, "jpg")

        pathReference.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.toString())
            val imageView = row.userimageView as ImageView
            imageView.setImageBitmap(bitmap)
        }.addOnFailureListener {
            // Handle any errors
        }



        return row
    }
}