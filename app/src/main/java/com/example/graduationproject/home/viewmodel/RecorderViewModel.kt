package com.example.graduationproject.home.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graduationproject.home.repo.RecorderRepo
import kotlinx.coroutines.launch
import java.net.URI

class RecorderViewModel(val recorderRepo: RecorderRepo): ViewModel()  {
    private val _transcription = MutableLiveData<String>()
    val transcription: LiveData<String> = _transcription
    fun getTranscription(url: Uri?) {

            viewModelScope.launch {
                val response = recorderRepo.getTranscription(url)
                _transcription.value=response.transcription
            }
    }
}