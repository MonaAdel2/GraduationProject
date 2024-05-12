package com.example.graduationproject.home.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.graduationproject.R
import com.example.graduationproject.authentication.signup.model.UserData
import com.example.graduationproject.databinding.FragmentHomeBinding
import com.example.graduationproject.databinding.FragmentUsersBinding
import com.example.graduationproject.home.adapter.UsersAdapter
import com.example.graduationproject.home.viewmodel.UserFragmentViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.firestore


class UsersFragment : Fragment() {
    private lateinit var binding: FragmentUsersBinding
    private lateinit var usersRV: RecyclerView
    private lateinit var userFragmentViewModel: UserFragmentViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       val db= FirebaseAuth.getInstance()
        val user = db.currentUser
        userFragmentViewModel=UserFragmentViewModel()
        userFragmentViewModel.getUsers()
        usersRV= binding.rvUsers


        userFragmentViewModel.userList.observe(requireActivity()){
            val adapter=UsersAdapter(it,requireContext())
            usersRV.adapter= adapter
            usersRV.layoutManager = LinearLayoutManager(requireContext(),  RecyclerView.VERTICAL, false)
            adapter.setOnClickListener(object : UsersAdapter.OnItemClickListener {
                override fun onItemClicked(userData: UserData) {
//                    val  action = UsersFragmentDirections.actionUsersFragmentToChatFragment2(user?.uid.toString(),userData)
                    val  action = UsersFragmentDirections.actionUsersFragmentToChatFragment2(userData)
                    findNavController().navigate(action)
                }
            })
        }

    }
}