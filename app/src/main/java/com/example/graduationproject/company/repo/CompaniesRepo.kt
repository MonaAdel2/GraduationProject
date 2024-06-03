package com.example.graduationproject.company.repo

import android.net.Uri
import com.example.graduationproject.company.model.CompanyData
import com.example.graduationproject.company.model.CompanyTranscription
import com.example.graduationproject.company.model.CompanyTranscriptionWithAudio

interface CompaniesRepo {
    suspend fun getListOfCompanies(): List<CompanyData>
    suspend fun getCompanyWithText(companyName: String): CompanyTranscription
     suspend fun getCompanyWithAudio(url: Uri?): CompanyTranscriptionWithAudio
}