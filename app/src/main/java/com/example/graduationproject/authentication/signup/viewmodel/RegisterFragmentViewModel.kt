package com.example.graduationproject.authentication.signup.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graduationproject.Utils
import com.google.firebase.Firebase
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.UUID

class RegisterFragmentViewModel(val context: Context):ViewModel() {
    private val imageRef = Firebase.storage.reference
    val db = Firebase.firestore
    private val _userAdded = MutableLiveData<Boolean>()
    val userAdded: LiveData<Boolean> = _userAdded

    // new variables
    val TAG = "RegisterFragmentViewModel"
    private var storage: FirebaseStorage = FirebaseStorage.getInstance()
    private var storageRef: StorageReference = storage.reference
    private var _uri = MutableLiveData<Uri>()
    private var uri: LiveData<Uri> = _uri
    private var bitmap: Bitmap? = null

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

    // new function to upload the image and retreive the uri string
    fun uploadImageToFirebaseStorage(imageBitmap: Bitmap?,onSuccess: (imageUrl: String) -> Unit) {
        viewModelScope.launch {
            if (imageBitmap != null) {
                val baos = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()

                bitmap = imageBitmap
//            binding.btnUploadImgRegister.setImageBitmap(imageBitmap)
                val storagePath = storageRef.child("photos/${UUID.randomUUID()}.jpg")
                val uploadTask = storagePath.putBytes(data)
                uploadTask.addOnSuccessListener { taskSnapshot ->
                    storagePath.downloadUrl.addOnSuccessListener { _uriReturned ->
                        val imageUrl = _uriReturned.toString()
                        _uri.value = _uriReturned
                        onSuccess(imageUrl) // Pass the imageUrl to the callback function
                    }
                }.addOnFailureListener { exception ->
                    Log.d(TAG, "uploadImageToFirebaseStorage: the image can't be uploaded.")
                }
            } else {
                // If imageBitmap is null, don't attempt to upload the image
                Log.d(TAG, "uploadImageToFirebaseStorage: the image is null")
            }
        }

    }



    fun addUser(user:HashMap<String,String>,phoneNumber:String,phoneId:String){
        viewModelScope.launch {
            db.collection("Users").document(Utils.getUidLoggedIn()).set(user)
                .addOnSuccessListener {
                    Toast.makeText(context, "user added", Toast.LENGTH_SHORT).show()
                    _userAdded.value=true
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