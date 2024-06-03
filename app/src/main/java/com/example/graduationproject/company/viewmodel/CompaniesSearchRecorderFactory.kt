package com.example.graduationproject.company.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.graduationproject.company.repo.CompaniesRepo

class CompaniesSearchRecorderFactory(private val companiesRepo: CompaniesRepo): ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(CompaniesSearchRecorderViewModel::class.java)) {
            CompaniesSearchRecorderViewModel(companiesRepo) as T
        }else{
            throw IllegalArgumentException("companies SearchRecorder view model class not found")
        }
    }

}