package com.example.graduationproject.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graduationproject.authentication.signup.model.UserData
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch

class UserFragmentViewModel(): ViewModel() {
    var db = Firebase.firestore
    private val _usersList = MutableLiveData<List<UserData?>>()
    val userList: LiveData<List<UserData?>> = _usersList
    fun getUsers(){
        viewModelScope.launch {
            var users : MutableList<UserData?> =mutableListOf()
            db.collection("Users").get()
                .addOnSuccessListener {
                    if(!it.isEmpty){
                        for(data in it.documents){
                            var user: UserData? = UserData(
                                data.data?.get("userName").toString(),
                                data.data?.get("phoneNumber").toString()
                                ,    data.data?.get("password").toString(),
                                data.data?.get("imageUri").toString()
                               , (data.data?.get("userID").toString())
                            )
                             users.add(user)
                        }
                        _usersList.value=users
                    }
                }
        }
    }
}