package com.example.graduationproject.authentication.signup.viewmodel

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RegisterFragmentViewModel(val context: Context):ViewModel() {
    private val imageRef = Firebase.storage.reference
    val db = Firebase.firestore
    fun uploadImageToStorage(imageUri:Uri,fileName:String, context: Context){
        viewModelScope.launch {
            try {
                imageUri?.let { it->
                    imageRef.child("images/$fileName").putFile(it).await()
                }
            }catch (e:Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
    fun addUser(user:HashMap<String,String>,phoneNumber:String,phoneId:String){
        viewModelScope.launch {
            db.collection("Users").add(user)
                .addOnSuccessListener {
                    Toast.makeText(context, "user added", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "failed $e", Toast.LENGTH_SHORT).show()
                }
            val userPhoneNumber = hashMapOf(
                "userPhoneNumber" to phoneNumber,
                "userPhoneId" to phoneId
            )
            db.collection("PhoneNumbers").add(userPhoneNumber).addOnSuccessListener {
                Toast.makeText(context, "phone added", Toast.LENGTH_SHORT).show()

            }.addOnFailureListener { e ->
                Toast.makeText(context, "phone failed $e", Toast.LENGTH_SHORT).show()
            }
        }
    }
}