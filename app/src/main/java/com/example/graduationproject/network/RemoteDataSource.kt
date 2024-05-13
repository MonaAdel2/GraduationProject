package com.example.graduationproject.network

import android.net.Uri
import com.example.graduationproject.home.model.TranscriptionResponse
import java.net.URI
import java.net.URL

interface RemoteDataSource {

        suspend fun getTranscription(url: Uri?): TranscriptionResponse

}