package com.example.graduationproject.company.repo

import android.util.Log
import com.example.graduationproject.company.model.CompanyData
import com.example.graduationproject.company.model.CompanyTranscription
import com.example.graduationproject.network.RemoteDataSource

class CompaniesRepoImp(val remoteDataSource: RemoteDataSource) :CompaniesRepo {
    override suspend fun getListOfCompanies(): List<CompanyData> {
        return  remoteDataSource.getListOfCompanies()
    }

    override suspend fun getCompanyWithText(companyName: String): CompanyTranscription {
        Log.d("CompaniesRepoImp", "getCompanyWithText: $companyName")
        return  remoteDataSource.getCompanyWithText(companyName)
    }
}