package com.example.graduationproject.home.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.R
import com.example.graduationproject.databinding.FragmentHomeBinding
import com.example.graduationproject.databinding.FragmentRegisterBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton


class HomeFragment : Fragment() {
 private lateinit var userBtn: FloatingActionButton
private lateinit var  binding : FragmentHomeBinding
private lateinit var recordBtn: FloatingActionButton
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userBtn=binding.btnShowUsers
        userBtn.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToUsersFragment()
            findNavController().navigate(action)
        }
        recordBtn=binding.btnRecorder
        recordBtn.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToRecorderFragment()
            findNavController().navigate(action)
        }


    }
}