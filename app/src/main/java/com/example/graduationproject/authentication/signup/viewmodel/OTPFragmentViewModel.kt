package com.example.graduationproject.authentication.signup.viewmodel

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.launch

class OTPFragmentViewModel(val context: Context, val activity: Activity):ViewModel() {
    private var auth = FirebaseAuth.getInstance()
    private val _otpVerified = MutableLiveData<Boolean>()
    val otpVerified: LiveData<Boolean> = _otpVerified


    fun verifyOtp(verificationId:String,typedOTP:String,){
        viewModelScope.launch {
            val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential( verificationId, typedOTP)
            signInWithPhoneAuthCredential(credential)
        }
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener(activity) { task ->
            if (task.isSuccessful) {
                val user = task.result?.user
                //Toast.makeText(context, "Authenticated successfully", Toast.LENGTH_SHORT).show()
                _otpVerified.value=true

            } else {
                if (task.exception is FirebaseAuthInvalidCredentialsException) {

                }
            }

        }
    }
    val  callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d("TAG", "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.w("TAG", "onVerificationFailed", e)
            if (e is FirebaseAuthInvalidCredentialsException) {
            } else if (e is FirebaseTooManyRequestsException) {
            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {

            }
        }
        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            Log.d("TAG", "onCodeSent:$verificationId")
            var Otp = verificationId
            var Otptoken= token
        }
    }
}