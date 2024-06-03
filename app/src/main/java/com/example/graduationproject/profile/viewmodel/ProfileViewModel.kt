package com.example.graduationproject.profile.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graduationproject.Utils
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.UUID

class ProfileViewModel(): ViewModel() {
    val TAG = "ProfileViewModel"
    private val _userNameUpdated = MutableLiveData<String>()
    val userNameUpdated: LiveData<String> = _userNameUpdated
    private val _passwordUpdated = MutableLiveData<String>()
    val passwordUpdated: LiveData<String> = _passwordUpdated
   private val db = Firebase.firestore
    private val storage = FirebaseStorage.getInstance()
    private var bitmap: Bitmap? = null
    private var storageRef: StorageReference = storage.reference
    fun updateUserName(userId: String, newValue: String){
        viewModelScope.launch {
            val docRef = db.collection("Users").document(userId)
            docRef.update("userName", newValue)
                .addOnSuccessListener {
                    _userNameUpdated.value=newValue
                }
                .addOnFailureListener { e ->
                    Log.e("FirestoreUpdate", "Error updating document", e)
                }
        }
    }
    fun updatePassword(userId: String, newValue: String){
        viewModelScope.launch {
            val docRef = db.collection("Users").document(userId)

            docRef.update("password", newValue)
                .addOnSuccessListener {
                    _passwordUpdated.value=newValue
                }
                .addOnFailureListener { e ->
                    Log.e("FirestoreUpdate", "Error updating document", e)
                }
        }
    }
     fun updateProfileInStorage(userId: String, newProfileUri: Uri) {
        val storageRef = storage.reference.child("images").child("$userId.jpg")

        storageRef.putFile(newProfileUri)
            .addOnSuccessListener {
                Log.d("ProfileUpdate", "Profile image uploaded successfully")
            }
            .addOnFailureListener { e ->
                Log.e("ProfileUpdate", "Error uploading profile image", e)
            }
    }

     fun updateProfileInFirestore(userId: String, newProfileUrl: String) {
        val docRef = db.collection("Users").document(userId)

        val updates = hashMapOf<String, Any>(
            "imageUri" to newProfileUrl
        )

        docRef.update(updates)
            .addOnSuccessListener {
                Log.d("FirestoreUpdate", "Profile URL successfully updated in Firestore!")
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreUpdate", "Error updating profile URL in Firestore", e)
            }
    }

    fun uploadImageToFirebaseStorage(imageBitmap: Bitmap?, onSuccess: (imageUrl: String) -> Unit) {

        viewModelScope.launch {
            if (imageBitmap != null) {
                val baos = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()

//                val uniqueID = listOf(Utils.getUidLoggedIn(), friendId).sorted()
//                uniqueID.joinToString(separator = "")

                bitmap = imageBitmap
//            binding.btnUploadImgRegister.setImageBitmap(imageBitmap)
                val storagePath = storageRef.child("images/${UUID.randomUUID()}.jpg")
                val uploadTask = storagePath.putBytes(data)
                uploadTask.addOnSuccessListener { taskSnapshot ->
                    storagePath.downloadUrl.addOnSuccessListener { imageLink ->
                        val imageUrl = imageLink.toString()
//                        _uri.value = imageLink
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