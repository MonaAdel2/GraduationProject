package com.example.graduationproject.authentication.signup.viewmodel

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.authentication.signup.model.PhoneData
import com.example.graduationproject.authentication.signup.view.PhoneFragment
import com.example.graduationproject.authentication.signup.view.PhoneFragmentDirections
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class PhoneFragmentViewModel(val context: Context,val activity:Activity) :ViewModel() {
    private val _otpSent = MutableLiveData<Boolean>()
    val otpSent: LiveData<Boolean> = _otpSent
    private val _phoneData = MutableLiveData<PhoneData>()
    val phoneData: LiveData<PhoneData> = _phoneData
    private val _phoneExists = MutableLiveData<Boolean>()
    val phoneExists: LiveData<Boolean> = _phoneExists
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    fun phoneAuth(phoneNumber: String) {
        viewModelScope.launch {
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                /*Sets the callbacks to be invoked during the phone number verification process. callbacks is an instance of OnVerificationStateChangedCallbacks, which is an interface provided by Firebase Authentication. It defines methods that will be called to handle different verification events, such as when a code is sent, verification completed, or verification failed.*/
                .setCallbacks(callbacks)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)

        }
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            if (e is FirebaseAuthInvalidCredentialsException) {
                Log.d("TAG", "onVerificationFailed: $e")
            } else if (e is FirebaseTooManyRequestsException) {
                Log.d("TAG", "onVerificationFailed: $e")
            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                Log.d("TAG", "onVerificationFailed: $e")
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {

            val storedVerificationId = verificationId
            val resendToken = token
            _otpSent.value = true
            _phoneData.value = PhoneData(token, verificationId)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener(activity) { task ->
            if (task.isSuccessful) {
                val user = task.result?.user
            } else {
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    Log.d("exception", "signInWithPhoneAuthCredential:${task.exception} ")
                }
            }
        }
    }

    fun checkPhoneExistsOrNot(phoneNumber: String) {
        viewModelScope.launch {
            var flag: Boolean = false
            db.collection("PhoneNumbers").get().addOnSuccessListener() { result ->
                for (document in result) {
                    if (phoneNumber == document.get("userPhoneNumber")) {
                        flag  = true
                        break
                    }else{
                        flag=false
                    }
                }
                _phoneExists.value=flag
            }
        }
    }
}
