package com.example.graduationproject.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.graduationproject.R
import com.example.graduationproject.authentication.signup.model.UserData
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.storage.FirebaseStorage

class UsersAdapter(private val data: List<UserData?>,private val context: Context): RecyclerView.Adapter<UsersAdapter.myHolder>() {
    private lateinit var onItemClickListener: OnItemClickListener

    interface  OnItemClickListener{
        fun onItemClicked(userData: UserData)
    }
    fun setOnClickListener(listener: OnItemClickListener){
        onItemClickListener = listener

    }
    class myHolder(row: View, onItemClickListener: OnItemClickListener): ViewHolder(row){
        var userName= row.findViewById<TextView>(R.id.tv_username)
        var userPicture=row.findViewById<ImageView>(R.id.iv_user_picture_in_rv)
        var openChatButton= row.findViewById<FloatingActionButton>(R.id.btn_adding_chat)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myHolder {
        val row = LayoutInflater.from(parent.context).inflate(R.layout.users_chat_item,parent,false)
        return myHolder(row,onItemClickListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: myHolder, position: Int) {
     /*   val storage = FirebaseStorage.getInstance()
        val gsReference = storage.getReference("/images/+201063028373")
        gsReference.child("/images/+201063028373").downloadUrl.addOnSuccessListener { url->
            Glide.with(context)
                .load( url)
                .into(holder.userPicture)
        }.addOnFailureListener {
            // Handle any errors
        }*/
        holder.userName.text= data[position]?.userName
        /*    Toast.makeText(context, "${data[position]?.imageUri}", Toast.LENGTH_SHORT).show()
            Glide.with(context)
                .load( gsReference.downloadUrl)
                .into(holder.userPicture)*/
        holder.openChatButton.setOnClickListener {
            onItemClickListener.onItemClicked(UserData(data[position]!!.userName,data[position]!!.phoneNumber,data[position]!!.password,data[position]!!.imageUri,data[position]!!.userId))
        }
    }
}