package com.example.graduationproject.company.repo

import com.example.graduationproject.company.model.CompanyData
import com.example.graduationproject.company.model.CompanyTranscription

interface CompaniesRepo {
    suspend fun getListOfCompanies(): List<CompanyData>
    suspend fun getCompanyWithText(companyName: String): CompanyTranscription
}