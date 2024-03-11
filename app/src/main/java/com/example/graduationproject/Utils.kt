package com.example.graduationproject

import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Utils {
    companion object {
        private val auth = FirebaseAuth.getInstance()
        private var userId: String = ""

        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_IMAGE_PICK = 2


        fun getUidLoggedIn(): String {

            if (auth.currentUser != null) {
                userId = auth.currentUser!!.uid
            }

            return userId
        }

        fun getTime(): String {
            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            val currentTime = Date()
            return dateFormat.format(currentTime)
        }


    }
}