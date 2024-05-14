package com.example.graduationproject.home.viewmodel

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graduationproject.authentication.signup.model.UserData
import com.example.graduationproject.home.repo.RecorderRepo
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.net.URI

class RecorderViewModel(val recorderRepo: RecorderRepo): ViewModel()  {
    private val _transcription = MutableLiveData<String>()
    val transcription: LiveData<String> = _transcription
    private val _searchedName = MutableLiveData<UserData>()
    val searchedName: LiveData<UserData> = _searchedName

    private val db = FirebaseFirestore.getInstance()
    fun getTranscription(url: Uri?) {

            viewModelScope.launch {
                val response = recorderRepo.getTranscription(url)
                _transcription.value=response.transcription
            }
    }
    fun searchDocumentsByName(name: String) {
        viewModelScope.launch {
            Log.d("RecorderViewModel", "searchDocumentsByName: ")
            db.collection("Users")
                .whereEqualTo("userName", name)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.d("RecorderViewModel", "Document found: ${document.id}")
                        val data = document.data
                      //  UserData( data["userName"] as String, data["phoneNumber"] as String, data["password"] as String, data["imageUri"] as String, data["userId"] as String)
                        _searchedName.value =  UserData( data["userName"] as String, data["phoneNumber"] as String, data["password"] as String, data["imageUri"] as String, data["userId"] as String)

                        Log.d("RecorderViewModel", "Document data: $data")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("RecorderViewModel", "Error searching documents", exception)
                }
        }
    }
}