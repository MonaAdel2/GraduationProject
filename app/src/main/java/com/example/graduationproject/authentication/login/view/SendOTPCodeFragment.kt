package com.example.graduationproject.authentication.login.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.MainActivity
import com.example.graduationproject.authentication.login.viewModel.AuthViewModel
import com.example.graduationproject.databinding.FragmentSendOtpCodeBinding
import com.google.firebase.auth.FirebaseAuth
import com.hbb20.CountryCodePicker

class SendOTPCodeFragment : Fragment() {
    private val TAG = "SendOTPCodeFragment"

    private var _binding: FragmentSendOtpCodeBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel
    private lateinit var ccp: CountryCodePicker
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSendOtpCodeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        if (auth.currentUser != null) {
            val intent = Intent(requireContext(), MainActivity::class.java)
            requireActivity().startActivity(intent)
            requireActivity().finish()
        }
        ccp = binding.countryCode
        ccp.registerCarrierNumberEditText(binding.phoneNumberEditText.editText)

        binding.sendOtpButton.setOnClickListener {
            val phoneNumber = binding.phoneNumberEditText.editText?.text.toString()
            if (phoneNumber.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please enter a valid phone number.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d(TAG, "onViewCreated: enter valid number")
            } else {
                val phone = ccp.fullNumberWithPlus
                authViewModel.sendVerificationCode(requireActivity(), phone, false)
                Log.d(TAG, "onCreate: the button clicked and the function is called")
            }

        }
        authViewModel.verificationCode.observe(viewLifecycleOwner) {
            if (it != null) {
                Log.d(TAG, "onCreate: it is the verification id : $it")
            val action =
                  SendOTPCodeFragmentDirections.actionSendOTPCodeFragmentToLoginFragment(it)
             findNavController().navigate(action)

            } else {
                Log.d(TAG, "onCreate: the verification id is null")
            }

        }

    }


}