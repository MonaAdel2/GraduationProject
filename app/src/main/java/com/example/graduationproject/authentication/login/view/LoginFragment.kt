package com.example.graduationproject.authentication.login.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.graduationproject.MainActivity
import com.example.graduationproject.R
import com.example.graduationproject.authentication.login.viewModel.AuthViewModel
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



            // new code for the otp taken input
//            val typedOTP = binding.etNo1.text.toString()+ binding.etNo2.text.toString() + binding.etNo3.text.toString()+ binding.etNo4.text.toString() + binding.etNo5.text.toString() + binding.etNo6.text.toString()
//            if(typedOTP.isNotEmpty()){
//                if(typedOTP.length==6){
//                    authViewModel.verifyCode(args.VerificationCode!!,typedOTP)
//                    Toast.makeText(requireContext(), "done", Toast.LENGTH_SHORT).show()
//                }else{
//                    Toast.makeText(requireContext(), "Please enter all Otp", Toast.LENGTH_SHORT).show()
//                }
//            }else{
//                Toast.makeText(requireContext(), "Please enter Otp", Toast.LENGTH_SHORT).show()
//            }



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

//    inner class EditTextWatcher (private val view:View ): TextWatcher {
//        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//        }
//
//        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            when(view.id){
//                R.id.et_no1 -> binding.etNo1.setBackgroundResource(R.drawable.digit_clicked_background)
//                R.id.et_no2 -> binding.etNo2.setBackgroundResource(R.drawable.digit_clicked_background)
//                R.id.et_no3 -> binding.etNo3.setBackgroundResource(R.drawable.digit_clicked_background)
//                R.id.et_no4 -> binding.etNo4.setBackgroundResource(R.drawable.digit_clicked_background)
//                R.id.et_no5 -> binding.etNo5.setBackgroundResource(R.drawable.digit_clicked_background)
//                R.id.et_no6 -> binding.etNo6.setBackgroundResource(R.drawable.digit_clicked_background)
//            }
//        }
//
//        override fun afterTextChanged(p0: Editable?) {
//            val text=p0.toString()
//            when(view.id){
//                R.id.et_no1 -> if(text.length==1) binding.etNo2.requestFocus()
//                R.id.et_no2 -> if(text.length==1) binding.etNo3.requestFocus() else if (text.isEmpty()){
//                    binding.etNo1.requestFocus()
//                    binding.etNo2.setBackgroundResource(R.drawable.digit_background)
//                }
//                R.id.et_no3 -> if(text.length==1) binding.etNo4.requestFocus()  else if (text.isEmpty()) {
//                    binding.etNo2.requestFocus()
//                    binding.etNo3.setBackgroundResource(R.drawable.digit_background)
//                }
//                R.id.et_no4 -> if(text.length==1) binding.etNo5.requestFocus() else if (text.isEmpty()) {
//                    binding.etNo3.requestFocus()
//                    binding.etNo4.setBackgroundResource(R.drawable.digit_background)
//                }
//                R.id.et_no5 -> if(text.length==1) binding.etNo6.requestFocus() else if (text.isEmpty()) {
//                    binding.etNo4.requestFocus()
//                    binding.etNo5.setBackgroundResource(R.drawable.digit_background)
//                }
//                R.id.et_no6 -> if (text.isEmpty()){
//                    binding.etNo5.requestFocus()
//                    binding.etNo6.setBackgroundResource(R.drawable.digit_background)
//                }
//            }
//        }
//
//
//    }
//    private fun addTextChangeListener(){
//        binding.etNo1.addTextChangedListener(EditTextWatcher(binding.etNo1))
//        binding.etNo2.addTextChangedListener(EditTextWatcher(binding.etNo2))
//        binding.etNo3.addTextChangedListener(EditTextWatcher(binding.etNo3))
//        binding.etNo4.addTextChangedListener(EditTextWatcher(binding.etNo4))
//        binding.etNo5.addTextChangedListener(EditTextWatcher(binding.etNo5))
//        binding.etNo6.addTextChangedListener(EditTextWatcher(binding.etNo6))
//    }


}