package com.example.graduationproject.authentication.signup.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.R

class PhoneFragment : Fragment() {
    lateinit var verifyBtn : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_phone, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        verifyBtn= view.findViewById(R.id.btn_send_code)
        verifyBtn.setOnClickListener {
            findNavController().navigate(R.id.action_phoneFragment_to_registerFragment)
        }
    }
}