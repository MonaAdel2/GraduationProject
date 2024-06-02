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
    private val _usersList = MutableLiveData<List<UserData?>>()
    val userList: LiveData<List<UserData?>> = _usersList

    private val db = FirebaseFirestore.getInstance()
    fun getTranscription(url: Uri?) {
            viewModelScope.launch {
                val response = recorderRepo.getTranscription(url)
                _transcription.value=response.transcription
            }
    }
    fun searchUsersByUserNamePrefix(prefix: String) {
        viewModelScope.launch {
            db.collection("Users")
                .orderBy("userName")
                .startAt(prefix)
                .endAt(prefix + "\uf8ff")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val users = querySnapshot.documents.mapNotNull { document ->
                        document.toObject(UserData::class.java)
                    }
                    _usersList.value = users
                }
                .addOnFailureListener { exception ->
                    Log.w("UserFragmentViewModel", "Error getting documents: ", exception)
                }
        }
    }
}