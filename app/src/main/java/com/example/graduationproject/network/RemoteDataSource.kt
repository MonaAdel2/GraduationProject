package com.example.graduationproject.network

import android.net.Uri
import com.example.graduationproject.company.model.CompanyData
import com.example.graduationproject.company.model.CompanyTranscription
import com.example.graduationproject.company.model.CompanyTranscriptionWithAudio
import com.example.graduationproject.home.model.TranscriptionResponse
import retrofit2.http.Query
import java.net.URI
import java.net.URL

interface RemoteDataSource {

        suspend fun getTranscription(url: Uri?): TranscriptionResponse
        suspend fun getCompanyWithAudio(url: Uri?): CompanyTranscriptionWithAudio
        suspend fun getCompanyWithText(companyName: String): CompanyTranscription
        suspend fun getListOfCompanies(): List<CompanyData>
}