package com.example.graduationproject.home.repo

import android.net.Uri
import com.example.graduationproject.home.model.TranscriptionResponse
import com.example.graduationproject.network.RemoteDataSource
import java.net.URI
import java.net.URL

class RecorderRepoImp(private val remoteDataSource: RemoteDataSource): RecorderRepo {
    override suspend fun getTranscription(url: Uri?): TranscriptionResponse {
        return  remoteDataSource.getTranscription(url)
    }

}