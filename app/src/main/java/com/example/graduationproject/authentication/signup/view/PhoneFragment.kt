package com.example.graduationproject.authentication.signup.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.R
import com.example.graduationproject.authentication.signup.model.PhoneData
import com.example.graduationproject.databinding.FragmentPhoneBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class PhoneFragment : Fragment() {
    private lateinit var binding: FragmentPhoneBinding
    private lateinit var phoneNumber:String
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentPhoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth= FirebaseAuth.getInstance()
        phoneNumber = binding.etPhoneNumber.editText?.text?.trim().toString()
        binding.btnSendCode.setOnClickListener {
            if(binding.etPhoneNumber.editText?.text?.length!=10){
                binding.etPhoneNumber.editText?.error="Phone number isn't correct "
            }else{
                phoneNumber= "+" +binding.counteryCodePicker.selectedCountryCode.toString()+ binding.etPhoneNumber.editText?.text?.toString()
                Toast.makeText(requireContext(), "$phoneNumber", Toast.LENGTH_SHORT).show()
                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(phoneNumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(requireActivity())
                    /*Sets the callbacks to be invoked during the phone number verification process. callbacks is an instance of OnVerificationStateChangedCallbacks, which is an interface provided by Firebase Authentication. It defines methods that will be called to handle different verification events, such as when a code is sent, verification completed, or verification failed.*/
                    .setCallbacks(callbacks)
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }
        }
    }
  private val  callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
           // Log.d(TAG, "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
           // Log.w(TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                Log.d("TAG", "onVerificationFailed: $e")
            } else if (e is FirebaseTooManyRequestsException) {
                Log.d("TAG", "onVerificationFailed: $e")
            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                Log.d("TAG", "onVerificationFailed: $e")
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
    //        Log.d(TAG, "onCodeSent:$verificationId")

            // Save verification ID and resending token so we can use them later
          val  storedVerificationId = verificationId
            val  resendToken = token
            val action = PhoneFragmentDirections.actionPhoneFragmentToOTPFragment(PhoneData(token,verificationId,phoneNumber))
            findNavController().navigate(action)
        }
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener(requireActivity()){ task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                  //  Log.d(TAG, "signInWithCredential:success")

                    val user = task.result?.user
                } else {
                    // Sign in failed, display a message and update the UI
                //    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }

}