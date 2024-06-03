package com.example.graduationproject.company.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graduationproject.company.model.CompanyData
import com.example.graduationproject.company.model.CompanyTranscription
import com.example.graduationproject.company.model.CompanyTranscriptionWithAudio
import com.example.graduationproject.company.repo.CompaniesRepo
import kotlinx.coroutines.launch

class CompaniesSearchRecorderViewModel(val companiesRepo: CompaniesRepo) : ViewModel() {
    private val _companiesList = MutableLiveData<CompanyTranscriptionWithAudio>()
    val companiesList: LiveData<CompanyTranscriptionWithAudio> = _companiesList
    fun getCompanyWithAudio(url: Uri?) {
        viewModelScope.launch {
            Log.d("RecorderFragment", "Audio uploaded successfully    f. URL: $url")
            Log.d("RecorderFragment", "Audio uploaded successfully    ff. URL: ${companiesRepo.getCompanyWithAudio(url).result}")
            _companiesList.value= companiesRepo.getCompanyWithAudio(url)
        }
    }
}