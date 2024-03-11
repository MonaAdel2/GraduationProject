package com.example.graduationproject

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatViewModel(val context: Context): ViewModel() {

    private val TAG = "ChatViewModel"

    private val messageRepo = MessageRepo()

    private val name = MutableLiveData<String>()
    private val imageUrl = MutableLiveData<String>()
    private val message = MutableLiveData<String>()

    private val fireStore = FirebaseFirestore.getInstance()

    fun sendMessage(sender: String, receiver: String, friendName: String, friendImage: String) =
        viewModelScope.launch(Dispatchers.IO) {
//            val context = MyApplication.instance.applicationContext

            var hashmap = hashMapOf<String, Any>(
                "sender" to sender,
                "receiver" to receiver,
                "message" to message.value!!,
                "time" to Utils.getTime()
            )

            val uniqueID = listOf(sender, receiver).sorted()
            uniqueID.joinToString(separator = "")

            val friendNameSplit = friendName.split("\\s".toRegex())[0]
            val mySharedPrefs = SharedPrefs(context)
            mySharedPrefs.setValue("friendId", receiver)
            mySharedPrefs.setValue("chatRoomId", uniqueID.toString())
            mySharedPrefs.setValue("friendName", friendNameSplit)
            mySharedPrefs.setValue("friendImage", friendImage)


            // Messages -> [sender, receiver] -> chats -> every single message
            val tempMessage = message.value!!
            fireStore.collection("Messages").document(uniqueID.toString())
                .collection("Chats").document(Utils.getTime())
                .set(hashmap).addOnCompleteListener { task ->

                    val hashmapForRecent = hashMapOf<String, Any>(
                        "friendId" to receiver,
                        "time" to Utils.getTime(),
                        "sender" to Utils.getUidLoggedIn(),
                        "message" to message.value!!,
                        "friendImage" to friendImage,
                        "name" to friendName,
                        "person" to "you"
                    )

                    fireStore.collection("Conversation${Utils.getUidLoggedIn()}").document(receiver)
                        .set(hashmapForRecent)
                    Log.d(
                        TAG,
                        "sendMessage: create the recent chat for user ${Utils.getUidLoggedIn()}"
                    )

                    Log.d(TAG, "sendMessage: ${message.value!!}")
                    val conversationRef = fireStore.collection("Conversation${receiver}")
                        .document(Utils.getUidLoggedIn())
                        .get().addOnSuccessListener { documentSnapshot ->
                            Log.d(
                                TAG,
                                "sendMessage: get the reference to the collection of receiver"
                            )
                            if (documentSnapshot.exists()) {
                                // Document exists, perform the update
                                Log.d(TAG, "sendMessage: inside if condition of exists()")
                                Log.d(TAG, "sendMessage: in if ${message.value!!}")
                                fireStore.collection("Conversation${receiver}")
                                    .document(Utils.getUidLoggedIn())
                                    .update(
                                        "message", tempMessage,
                                        "time", Utils.getTime(),
                                        "person", name.value!!
                                    )

                            } else {
                                // Document does not exist, create it first
                                Log.d(TAG, "sendMessage: inside else ")
                                Log.d(TAG, "sendMessage: in else ${message.value!!}")
                                val data = hashMapOf(
                                    "friendId" to Utils.getUidLoggedIn(),
                                    "time" to Utils.getTime(),
                                    "sender" to Utils.getUidLoggedIn(),
                                    "message" to tempMessage,
                                    "friendImage" to imageUrl.value!!,
                                    "name" to name.value!!,
                                    "person" to name.value!!
                                )
                                fireStore.collection("Conversation${receiver}")
                                    .document(Utils.getUidLoggedIn()).set(data)

                            }

                        }

                    // to clear the edit text after pressing send
                    if (task.isSuccessful) {
                        message.value = ""
                    }

                }

        }

    fun getMessages(friendId: String): LiveData<List<Message>> {
        return messageRepo.getMessages(friendId)
    }
}
