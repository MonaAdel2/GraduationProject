package com.example.graduationproject.home.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.graphics.Color
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.graduationproject.authentication.signup.model.UserData

import com.example.graduationproject.databinding.FragmentUsersBinding
import com.example.graduationproject.home.adapter.UsersAdapter
import com.example.graduationproject.home.viewmodel.UserFragmentViewModel

import com.google.firebase.auth.FirebaseAuth




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
        val searchView = binding.svUsers
        val searchAutoComplete = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchAutoComplete.setTextColor(Color.parseColor("#FFFFFF"))
        searchAutoComplete.setHintTextColor(Color.parseColor("#FFFFFF"))
        val user = db.currentUser
        userFragmentViewModel=UserFragmentViewModel()
        userFragmentViewModel.getUsers()
        usersRV= binding.rvUsers
       var  userSv =binding.svUsers
        userFragmentViewModel.userList.observe(requireActivity()){
            val adapter=UsersAdapter(it,requireContext())
            usersRV.adapter= adapter
            usersRV.layoutManager = LinearLayoutManager(requireContext(),  RecyclerView.VERTICAL, false)
            adapter.setOnClickListener(object : UsersAdapter.OnItemClickListener {
                override fun onItemClicked(userData: UserData) {
                    val  action = UsersFragmentDirections.actionUsersFragmentToChatFragment2(userData)
                    findNavController().navigate(action)
                }
            })
        }
        userSv.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                userFragmentViewModel.searchUsersByUserNamePrefix(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {

                userFragmentViewModel.searchUsersByUserNamePrefix(newText)
                return true
            }
        })
    }
}