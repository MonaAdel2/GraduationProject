package com.example.graduationproject.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.graduationproject.Utils
import com.example.graduationproject.chat.model.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MessageRepo {

    private val TAG = "MessageRepo"

    private val fireStore = FirebaseFirestore.getInstance()

    fun getMessages(friendId: String): LiveData<List<Message>> {

        val messages = MutableLiveData<List<Message>>()

        val uniqueId = listOf(Utils.getUidLoggedIn(), friendId).sorted()
        uniqueId.joinToString(separator = "")

        fireStore.collection("Messages").document(uniqueId.toString()).collection("Chats")
            .orderBy("time", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, exception ->

                if (exception != null) {
                    return@addSnapshotListener
                }

                val messagesList = mutableListOf<Message>()

                if (!snapshot!!.isEmpty) {
                    snapshot.documents.forEach { document ->
                        val messageModel = document.toObject(Message::class.java)
                        Log.d(TAG, "getMessages: the message model returned => ${messageModel}")

                        Log.d(TAG, "getMessages: the flag is ${messageModel?.image}")
//                        Log.d(TAG, "getMessages: the flag is ${messageModel?.message}")

                        if (messageModel!!.sender.equals(Utils.getUidLoggedIn()) && messageModel.receiver.equals(friendId) ||
                            messageModel.sender.equals(friendId) && messageModel.receiver.equals(Utils.getUidLoggedIn())) {
                            messageModel.let {
                                messagesList.add(it)
                            }
                        }
                    }
                    messages.value = messagesList
                }
            }

        return messages

    }
}