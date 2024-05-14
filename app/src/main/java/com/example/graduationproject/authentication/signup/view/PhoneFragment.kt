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
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.R
import com.example.graduationproject.authentication.signup.model.PhoneData
import com.example.graduationproject.authentication.signup.viewmodel.PhoneFragmentViewModel
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var phoneFragmentViewModel= PhoneFragmentViewModel(requireContext(),requireActivity())
        phoneNumber = binding.etPhoneNumber.editText?.text?.trim().toString()
        phoneFragmentViewModel.phoneExists.observe(requireActivity()){ phoneExists->
            Toast.makeText(requireContext(), "$phoneExists", Toast.LENGTH_SHORT).show()
            if(phoneExists==true){
//                Toast.makeText(requireContext(), "you Already Have AN account", Toast.LENGTH_SHORT).show()
                val action= PhoneFragmentDirections.actionPhoneFragmentToSendOTPCodeFragment()
                findNavController().navigate(action)
            }else{
                Toast.makeText(requireContext(), "$phoneNumber", Toast.LENGTH_SHORT).show()
                phoneNumber= "+" +binding.counteryCodePicker.selectedCountryCode.toString()+ binding.etPhoneNumber.editText?.text?.toString()
                Toast.makeText(requireContext(), "$phoneNumber", Toast.LENGTH_SHORT).show()
                phoneFragmentViewModel.phoneAuth(phoneNumber)
            }
        }
        phoneFragmentViewModel.otpSent.observe(requireActivity()) { OtpSent ->
            if(OtpSent==true){
                phoneFragmentViewModel.phoneData.observe(requireActivity()){ phoneData->
                    Toast.makeText(requireContext(), "$phoneNumber", Toast.LENGTH_SHORT).show()
                    val action = PhoneFragmentDirections.actionPhoneFragmentToOTPFragment2(phoneData,phoneNumber)
                    findNavController().navigate(action)
                }
            }
        }
        binding.btnSendCode.setOnClickListener {
            if(binding.etPhoneNumber.editText?.text?.length!=10){
                binding.etPhoneNumber.editText?.error="Phone number isn't correct"
            }
            else{
              phoneNumber= "+" +binding.counteryCodePicker.selectedCountryCode.toString()+ binding.etPhoneNumber.editText?.text?.toString()
                phoneFragmentViewModel.checkPhoneExistsOrNot(phoneNumber)
            }
        }
        binding.tvGoToLogin.setOnClickListener{
            val action = PhoneFragmentDirections.actionPhoneFragmentToSendOTPCodeFragment()
            view?.findNavController()?.navigate(action)
        }
    }
}