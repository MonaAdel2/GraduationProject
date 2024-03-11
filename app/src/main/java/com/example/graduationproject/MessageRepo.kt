package com.example.graduationproject

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MessageRepo {

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

                        if (messageModel!!.sender.equals(Utils.getUidLoggedIn()) && messageModel.receiver.equals(
                                friendId
                            ) ||
                            messageModel.sender.equals(friendId) && messageModel.receiver.equals(
                                Utils.getUidLoggedIn()
                            )
                        ) {
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