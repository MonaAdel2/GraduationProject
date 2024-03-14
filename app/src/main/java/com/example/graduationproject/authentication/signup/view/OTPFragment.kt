package com.example.graduationproject.authentication.signup.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.graduationproject.R
import com.example.graduationproject.databinding.FragmentOTPBinding
import com.example.graduationproject.databinding.FragmentPhoneBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider


class OTPFragment : Fragment() {
    lateinit var binding: FragmentOTPBinding
    val  navArgs : OTPFragmentArgs by navArgs()
    private lateinit var token : PhoneAuthProvider.ForceResendingToken
    private lateinit var phoneNumber: String
    private lateinit var verificationId : String
    private lateinit var auth :FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOTPBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        addTextChangeListener()

        token = navArgs.phoneData.token
        phoneNumber = navArgs.phoneData.phoneNumber
        verificationId=navArgs.phoneData.verificationId
        binding.btnVerify.setOnClickListener {
                val typedOTP = binding.etNo1.text.toString()+ binding.etNo2.text.toString() + binding.etNo3.text.toString()+ binding.etNo4.text.toString() + binding.etNo5.text.toString() + binding.etNo6.text.toString()
                if(typedOTP.isNotEmpty()){
                    if(typedOTP.length==6){
                        val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential( verificationId, typedOTP)
                        signInWithPhoneAuthCredential(credential)
                        Toast.makeText(requireContext(), "done", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(requireContext(), "Please enter all Otp", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(requireContext(), "Please enter Otp", Toast.LENGTH_SHORT).show()
                }

        }

    }
    inner class EditTextWatcher (private val view:View ): TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            when(view.id){
                R.id.et_no1 -> binding.etNo1.setBackgroundResource(R.drawable.digit_clicked_background)
                R.id.et_no2 -> binding.etNo2.setBackgroundResource(R.drawable.digit_clicked_background)
                R.id.et_no3 -> binding.etNo3.setBackgroundResource(R.drawable.digit_clicked_background)
                R.id.et_no4 -> binding.etNo4.setBackgroundResource(R.drawable.digit_clicked_background)
                R.id.et_no5 -> binding.etNo5.setBackgroundResource(R.drawable.digit_clicked_background)
                R.id.et_no6 -> binding.etNo6.setBackgroundResource(R.drawable.digit_clicked_background)
            }
        }

        override fun afterTextChanged(p0: Editable?) {
            val text=p0.toString()
            when(view.id){
                R.id.et_no1 -> if(text.length==1) binding.etNo2.requestFocus()
                R.id.et_no2 -> if(text.length==1) binding.etNo3.requestFocus() else if (text.isEmpty()){
                    binding.etNo1.requestFocus()
                    binding.etNo2.setBackgroundResource(R.drawable.digit_background)
                }
                R.id.et_no3 -> if(text.length==1) binding.etNo4.requestFocus()  else if (text.isEmpty()) {
                    binding.etNo2.requestFocus()
                    binding.etNo3.setBackgroundResource(R.drawable.digit_background)
                }
                R.id.et_no4 -> if(text.length==1) binding.etNo5.requestFocus() else if (text.isEmpty()) {
                    binding.etNo3.requestFocus()
                    binding.etNo4.setBackgroundResource(R.drawable.digit_background)
                }
                R.id.et_no5 -> if(text.length==1) binding.etNo6.requestFocus() else if (text.isEmpty()) {
                    binding.etNo4.requestFocus()
                    binding.etNo5.setBackgroundResource(R.drawable.digit_background)
                }
                R.id.et_no6 -> if (text.isEmpty()){
                    binding.etNo5.requestFocus()
                    binding.etNo6.setBackgroundResource(R.drawable.digit_background)
                }
            }
        }


    }
    private fun addTextChangeListener(){
        binding.etNo1.addTextChangedListener(EditTextWatcher(binding.etNo1))
        binding.etNo2.addTextChangedListener(EditTextWatcher(binding.etNo2))
        binding.etNo3.addTextChangedListener(EditTextWatcher(binding.etNo3))
        binding.etNo4.addTextChangedListener(EditTextWatcher(binding.etNo4))
        binding.etNo5.addTextChangedListener(EditTextWatcher(binding.etNo5))
        binding.etNo6.addTextChangedListener(EditTextWatcher(binding.etNo6))
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                val user = task.result?.user
                Toast.makeText(requireContext(), "Authenticated successfully", Toast.LENGTH_SHORT).show()
             /*   var action = OtpFragmentDirections.actionOtpFragmentToSignupFragment2(phoneNumber)
                findNavController().navigate(action)*/

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
