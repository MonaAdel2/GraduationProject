package com.example.graduationproject.network

import android.net.Uri
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


}