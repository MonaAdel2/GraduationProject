package com.example.graduationproject.chat.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graduationproject.SharedPrefs
import com.example.graduationproject.Utils
import com.example.graduationproject.chat.MessageRepo
import com.example.graduationproject.chat.model.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.UUID

class ChatViewModel(val context: Context): ViewModel() {

    private val TAG = "ChatViewModel"

    private val messageRepo = MessageRepo()

    private val name = MutableLiveData<String>()
    private val imageUrl = MutableLiveData<String>()
    val message = MutableLiveData<String>()

    private val fireStore = FirebaseFirestore.getInstance()
    private var storage: FirebaseStorage = FirebaseStorage.getInstance()
    private var storageRef: StorageReference = storage.reference

    private var _uri = MutableLiveData<Uri>()
    private var uri: LiveData<Uri> = _uri
    private var bitmap: Bitmap? = null


    init {
        fireStore.collection("Users").document(Utils.getUidLoggedIn()).get().addOnSuccessListener {
            if (it.exists()){
                name.value = it.getString("userName")
                imageUrl.value = it.getString("imageUri")
            }else{
                Log.d(TAG, "initial logic in view model: the document can't be found")
            }
        }
    }

    fun sendMessage(sender: String, receiver: String, friendName: String, friendImage: String) =
        viewModelScope.launch(Dispatchers.IO) {
//            val context = MyApplication.instance.applicationContext

            var hashmap = hashMapOf<String, Any>(
                "sender" to sender,
                "receiver" to receiver,
                "message" to message.value!!,
                "time" to Utils.getTime(),
                "image" to "false"
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
                        "person" to "you",
                        "image" to "false"
                    )

                    fireStore.collection("RecentChatsOf_${Utils.getUidLoggedIn()}").document(receiver)
                        .set(hashmapForRecent)
                    Log.d(
                        TAG,
                        "sendMessage: create the recent chat for user ${Utils.getUidLoggedIn()}"
                    )

                    Log.d(TAG, "sendMessage: ${message.value!!}")
                    val recentChatsRef = fireStore.collection("RecentChatsOf_${receiver}")
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
                                fireStore.collection("RecentChatsOf_${receiver}")
                                    .document(Utils.getUidLoggedIn())
                                    .update("message", tempMessage, "time", Utils.getTime(), "person", name.value!!, "image", "false")

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
                                    "person" to name.value!!,
                                    "image" to "false"
                                )
                                fireStore.collection("RecentChatsOf_${receiver}")
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

    fun sendMedia(sender: String, receiver: String, friendName: String, friendImage: String , imageUri: String){

        viewModelScope.launch {
            var hashmap = hashMapOf<String, Any>(
                "sender" to sender,
                "receiver" to receiver,
                "message" to imageUri,
                "time" to Utils.getTime(),
                "image" to "true"
            )

            val uniqueID = listOf(sender, receiver).sorted()
            uniqueID.joinToString(separator = "")

            val friendNameSplit = friendName.split("\\s".toRegex())[0]
            val mySharedPrefs = SharedPrefs(context)
            mySharedPrefs.setValue("friendId", receiver)
            mySharedPrefs.setValue("chatRoomId", uniqueID.toString())
            mySharedPrefs.setValue("friendName", friendNameSplit)
            mySharedPrefs.setValue("friendImage", friendImage)

            fireStore.collection("Messages").document(uniqueID.toString())
                .collection("Chats").document(Utils.getTime())
                .set(hashmap).addOnSuccessListener {task->
                    val hashmapForRecent = hashMapOf<String, Any>(
                        "friendId" to receiver,
                        "time" to Utils.getTime(),
                        "sender" to Utils.getUidLoggedIn(),
                        "message" to imageUri,
                        "friendImage" to friendImage,
                        "name" to friendName,
                        "person" to "you",
                        "image" to "true"
                    )

                    fireStore.collection("RecentChatsOf_${Utils.getUidLoggedIn()}").document(receiver)
                        .set(hashmapForRecent)
                    Log.d(TAG, "sendMessage: create the recent chat for user ${Utils.getUidLoggedIn()}")

//                    Log.d(TAG, "sendMessage: ${message.value!!}")
                    val recentChatsRef = fireStore.collection("RecentChatsOf_${receiver}")
                        .document(Utils.getUidLoggedIn())
                        .get().addOnSuccessListener { documentSnapshot ->
                            Log.d(TAG, "sendMessage: get the reference to the collection of receiver")
                            if (documentSnapshot.exists()) {
                                // Document exists, perform the update

                                fireStore.collection("RecentChatsOf_${receiver}")
                                    .document(Utils.getUidLoggedIn())
                                    .update(
                                        "message", imageUri,
                                        "time", Utils.getTime(),
                                        "person", name.value!!,
                                        "image" , "true"
                                    )

                            } else {
                                // Document does not exist, create it first
                                Log.d(TAG, "sendMessage: inside else ")
                                Log.d(TAG, "sendMessage: in else ${message.value!!}")
                                val data = hashMapOf(
                                    "friendId" to Utils.getUidLoggedIn(),
                                    "time" to Utils.getTime(),
                                    "sender" to Utils.getUidLoggedIn(),
                                    "message" to imageUri,
                                    "friendImage" to imageUrl.value!!,
                                    "name" to name.value!!,
                                    "person" to name.value!!,
                                    "image" to "true"
                                )
                                fireStore.collection("RecentChatsOf_${receiver}")
                                    .document(Utils.getUidLoggedIn()).set(data)

                            }

                        }
                }
        }

    }

    fun uploadImageToFirebaseStorage(imageBitmap: Bitmap?, friendId: String ,onSuccess: (imageUrl: String) -> Unit) {

        viewModelScope.launch {
            if (imageBitmap != null) {
                val baos = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()

                val uniqueID = listOf(Utils.getUidLoggedIn(), friendId).sorted()
                uniqueID.joinToString(separator = "")

                bitmap = imageBitmap
//            binding.btnUploadImgRegister.setImageBitmap(imageBitmap)
                val storagePath = storageRef.child("chats_media/${uniqueID}/images/${UUID.randomUUID()}.jpg")
                val uploadTask = storagePath.putBytes(data)
                uploadTask.addOnSuccessListener { taskSnapshot ->
                    storagePath.downloadUrl.addOnSuccessListener { imageLink ->
                        val imageUrl = imageLink.toString()
                        _uri.value = imageLink
                        onSuccess(imageUrl) // Pass the imageUrl to the callback function
                    }
                }.addOnFailureListener { exception ->
                    Log.d(TAG, "uploadImageToFirebaseStorage: Failed to upload the message ${exception.message}")
                }
            } else {
                // If imageBitmap is null, don't attempt to upload the image
                Log.d(TAG, "uploadImageToFirebaseStorage: Image is null")
            }
        }
        }

}
