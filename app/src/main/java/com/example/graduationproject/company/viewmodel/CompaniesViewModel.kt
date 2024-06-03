package com.example.graduationproject.company.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graduationproject.company.model.CompanyData
import com.example.graduationproject.company.model.CompanyTranscription
import com.example.graduationproject.company.repo.CompaniesRepo
import kotlinx.coroutines.launch

class CompaniesViewModel(val companiesRepo: CompaniesRepo): ViewModel() {
    private val _companiesList = MutableLiveData<List<CompanyData>>()
    val companiesList: LiveData<List<CompanyData>> = _companiesList


    fun getListOfCompanies(){
        viewModelScope.launch {
            _companiesList.value=   companiesRepo.getListOfCompanies()
        }
    }
     fun getCompanyWithText(companyName: String){
         viewModelScope.launch {
             _companiesList.value = companiesRepo.getCompanyWithText(companyName).result
         }
     }
}