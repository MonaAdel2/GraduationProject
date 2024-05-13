package com.example.graduationproject.network

import android.net.Uri
import com.example.graduationproject.home.model.TranscriptionResponse
import java.net.URI
import java.net.URL

object Client:RemoteDataSource {
    override suspend fun getTranscription(url: Uri?): TranscriptionResponse {
        return Helper.retrofit.create(Service::class.java).getTranscription(url)
    }
}