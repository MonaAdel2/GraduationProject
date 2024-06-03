package com.example.graduationproject.company.repo

import com.example.graduationproject.company.model.CompanyData

interface CompaniesRepo {
    suspend fun getListOfCompanies(): List<CompanyData>
}