package com.example.graduationproject.authentication.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.graduationproject.authentication.repo.AuthRepoImp

class AuthViewModelFactory(val repo: AuthRepoImp): ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        return if(modelClass.isAssignableFrom(AuthViewModel::class.java)){
//            AuthViewModel(repo) as T
//
//        }else{
//            throw IllegalArgumentException("AuthViewModel class not found")
//        }
//    }
}