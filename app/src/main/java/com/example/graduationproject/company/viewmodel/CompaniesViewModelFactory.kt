package com.example.graduationproject.company.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.graduationproject.company.repo.CompaniesRepo

class CompaniesViewModelFactory(private val companiesRepo: CompaniesRepo): ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(CompaniesViewModel::class.java)) {
            CompaniesViewModel(companiesRepo) as T
        }else{
            throw IllegalArgumentException("companies view model class not found")
        }
    }

}