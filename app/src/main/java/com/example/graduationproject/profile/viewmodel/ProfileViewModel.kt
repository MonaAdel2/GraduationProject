package com.example.graduationproject.profile.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage

import kotlinx.coroutines.launch

class ProfileViewModel(): ViewModel() {
    private val _userNameUpdated = MutableLiveData<String>()
    val userNameUpdated: LiveData<String> = _userNameUpdated
    private val _passwordUpdated = MutableLiveData<String>()
    val passwordUpdated: LiveData<String> = _passwordUpdated
   private val db = Firebase.firestore
    private val storage = FirebaseStorage.getInstance()
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
}