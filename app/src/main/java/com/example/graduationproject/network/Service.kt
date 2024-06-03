package com.example.graduationproject.network

import android.net.Uri
import com.example.graduationproject.company.model.CompanyData
import com.example.graduationproject.company.model.CompanyTranscription
import com.example.graduationproject.home.model.TranscriptionResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url
import java.net.URI
import java.net.URL

interface Service {

        @POST("transcribe")
        suspend fun getTranscription(@Query("audio_url") url: Uri?): TranscriptionResponse
        @POST("company_audio")
        suspend fun getCompanyWithAudio(@Query("audio_url") url: Uri?): CompanyTranscription

        @POST("company_text")
        suspend fun getCompanyWithText(@Query("company_name") companyName: String): CompanyTranscription

        @GET("companies")
        suspend fun getListOfCompanies(): List<CompanyData>
}