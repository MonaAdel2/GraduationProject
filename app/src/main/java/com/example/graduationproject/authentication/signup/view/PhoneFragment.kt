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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.R
import com.example.graduationproject.authentication.signup.model.PhoneData
import com.example.graduationproject.databinding.FragmentPhoneBinding
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
import com.google.firebase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class PhoneFragment : Fragment() {
    private lateinit var binding: FragmentPhoneBinding
    private lateinit var phoneNumber:String
    private lateinit var auth: FirebaseAuth
    private  val db = Firebase.firestore
    private var phoneExistFlag :Boolean =false

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
                binding.etPhoneNumber.editText?.error="Phone number isn't correct"
            }else if(checkPhoneExistsOrNot("+" +binding.counteryCodePicker.selectedCountryCode.toString()+ binding.etPhoneNumber.editText?.text?.toString()))
            {
                Toast.makeText(requireContext(), "${checkPhoneExistsOrNot("+" +binding.counteryCodePicker.selectedCountryCode.toString()+ binding.etPhoneNumber.editText?.text?.toString())} value", Toast.LENGTH_SHORT).show()
                val action = PhoneFragmentDirections.actionPhoneFragmentToSendOTPCodeFragment()
                findNavController().navigate(action)
            }
            else{
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
          val  storedVerificationId = verificationId
            val  resendToken = token
            val action = PhoneFragmentDirections.actionPhoneFragmentToOTPFragment(PhoneData(token,verificationId,phoneNumber))

            findNavController().navigate(action)
        }
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener(requireActivity()){ task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Log.d("exception", "signInWithPhoneAuthCredential:${task.exception} ")
                    }

                }
            }
    }
   private fun checkPhoneExistsOrNot(phoneNumber:String):Boolean{
       lifecycleScope.launch {
           db.collection("PhoneNumbers").get().addOnSuccessListener(){ result ->
               for(document in result){
                   if(phoneNumber==document.get("userPhoneNumber")){
                       Toast.makeText(requireContext(), "user has an account", Toast.LENGTH_SHORT).show()
                       phoneExistFlag= true
                       Toast.makeText(requireContext(), "$phoneExistFlag value is ", Toast.LENGTH_SHORT).show()
                       break
                   }else {
                       phoneExistFlag = false
                   }
               }
           }
       }
       Toast.makeText(requireContext(), "$phoneExistFlag value is ", Toast.LENGTH_SHORT).show()

       return phoneExistFlag
    }
}