package com.example.graduationproject.authentication.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.graduationproject.MainActivity
import com.example.graduationproject.authentication.viewModel.AuthViewModel
import com.example.graduationproject.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private val TAG = "LoginFragment"
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel

    private val args: LoginFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        binding.btnVerifyOtp.setOnClickListener {
//            val otp = binding.etOtpCode.text.toString()
            val otp = binding.etOtpCode.editText?.text.toString()
            if (otp.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter OTP", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "onViewCreated: enter valid otp")
            } else {
                val verificationCode = args.VerificationCode
                if (verificationCode != null) {
                    authViewModel.verifyCode(args.VerificationCode!!, otp)
                } else {
                    Log.d(TAG, "onCreate: No verification code is found")
                }

            }

        }

        authViewModel.isVerified.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it) {
                    Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                    // go to the next activity
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    requireActivity().startActivity(intent)
                    requireActivity().finish()
                } else {
                    Toast.makeText(requireContext(), "Fail", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.d(TAG, "onCreate: the isVerified is null")
            }

        }

    }


}