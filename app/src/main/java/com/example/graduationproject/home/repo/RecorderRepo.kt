package com.example.graduationproject.home.repo

import android.net.Uri
import com.example.graduationproject.home.model.TranscriptionResponse
import java.net.URI
import java.net.URL

interface RecorderRepo {
    suspend fun getTranscription(url: Uri?): TranscriptionResponse
}