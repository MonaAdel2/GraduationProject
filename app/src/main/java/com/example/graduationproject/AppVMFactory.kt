package com.example.graduationproject

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.graduationproject.chat.viewModel.ChatViewModel

class AppVMFactory(val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(ChatViewModel::class.java)){
            ChatViewModel(context) as T

        }else{
            throw IllegalArgumentException("ChatViewModel class not found")
        }
    }
}