package com.example.graduationproject.authentication.signup.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.graduationproject.R
import com.google.firebase.auth.PhoneAuthProvider


class OTPFragment : Fragment() {
    val  navArgs : OTPFragmentArgs by navArgs()
    private lateinit var token : PhoneAuthProvider.ForceResendingToken
    private lateinit var phoneNumber: String
    private lateinit var verificationId : String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_o_t_p, container, false)
    }

}