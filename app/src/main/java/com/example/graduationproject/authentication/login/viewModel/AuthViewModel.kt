package com.example.graduationproject.authentication.login.viewModel

import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AuthViewModel : ViewModel() {
    private val TAG = "AuthViewModel"
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _verificationCode = MutableLiveData<String>()
    val verificationCode: LiveData<String> get() = _verificationCode

    private var _isVerified = MutableLiveData<Boolean>()
    val isVerified: LiveData<Boolean> get() = _isVerified

    private var resendingToken: PhoneAuthProvider.ForceResendingToken? = null

    fun sendVerificationCode(activity: FragmentActivity, number: String, isResend: Boolean) {
        viewModelScope.launch {
            val options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(number)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(mCallBack)
                .build()

            if (isResend) {
                // need to search for how to use forceResendToken not to build options again
                PhoneAuthProvider.verifyPhoneNumber(options)
            } else {
                PhoneAuthProvider.verifyPhoneNumber(options)
            }
        }


    }


    private val mCallBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onCodeSent(
            s: String,
            forceResendingToken: PhoneAuthProvider.ForceResendingToken
        ) {
            super.onCodeSent(s, forceResendingToken)
            _verificationCode.value = s
            resendingToken = forceResendingToken
            Log.d(TAG, "onCodeSent: OTP is sent successfully.")
        }

        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
            val code = phoneAuthCredential.smsCode
            signIn(phoneAuthCredential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.d(TAG, "onVerificationFailed: ${e.message}")
        }
    }


    private fun signIn(phoneAuthCredential: PhoneAuthCredential) {
        viewModelScope.launch {
            mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener {
                if (it.isSuccessful) {
                    _isVerified.value = true

                } else {
                    Log.d(TAG, "signIn: OTP verfication failed... ${it.exception}")
                    _isVerified.value = false
                }
            }
        }

    }

    fun verifyCode(verificationCode: String, code: String) {
        viewModelScope.launch {
            val credential = PhoneAuthProvider.getCredential(verificationCode, code)
            signIn(credential)
        }

    }
}