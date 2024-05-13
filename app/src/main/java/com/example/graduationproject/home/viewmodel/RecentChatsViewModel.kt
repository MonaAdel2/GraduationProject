package com.example.graduationproject.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.graduationproject.home.model.RecentChat
import com.example.graduationproject.home.repo.RecentChatsListRepo

class RecentChatsViewModel: ViewModel() {

    val recentChatRepo = RecentChatsListRepo()

    fun getRecentChat(): LiveData<List<RecentChat>> {
        return recentChatRepo.getAllChatsList()
    }
}