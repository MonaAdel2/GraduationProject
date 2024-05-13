package com.example.graduationproject.home.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.graduationproject.Utils
import com.example.graduationproject.home.model.RecentChat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class RecentChatsListRepo {
    private val TAG = "ChatListRepo"

    private val firestore = FirebaseFirestore.getInstance()

    fun getAllChatsList(): LiveData<List<RecentChat>> {

        val mainChatList = MutableLiveData<List<RecentChat>>()

        firestore.collection("RecentChatsOf_${Utils.getUidLoggedIn()}")
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                val chatList = mutableListOf<RecentChat>()

                value?.forEach { document ->
                    val recentChatModel = document.toObject(RecentChat::class.java)
                    recentChatModel.let { recentChat ->
//                        firestore.collection("Users").document(recentChat.friendId!!).get()
//                            .addOnSuccessListener {
//                                if (it.exists()) {
//                                    val data = it.data
//
//
//                                }
//                            }
                        chatList.add(recentChat)
                    }
                }
                mainChatList.value = chatList
            }
        return mainChatList
    }

}