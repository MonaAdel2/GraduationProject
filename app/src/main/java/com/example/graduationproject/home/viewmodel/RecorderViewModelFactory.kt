package com.example.graduationproject.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.graduationproject.home.repo.RecorderRepo

class RecorderViewModelFactory(val recorderRepo: RecorderRepo): ViewModelProvider.Factory{

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(RecorderViewModel::class.java)) {
                RecorderViewModel(recorderRepo) as T
            }else{
                throw IllegalArgumentException("home view model class not found")
            }
        }

}