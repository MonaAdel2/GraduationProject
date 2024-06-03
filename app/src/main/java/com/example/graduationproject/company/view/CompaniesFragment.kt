package com.example.graduationproject.company

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.graduationproject.authentication.signup.model.UserData
import com.example.graduationproject.company.repo.CompaniesRepoImp
import com.example.graduationproject.company.viewmodel.CompaniesViewModel
import com.example.graduationproject.company.viewmodel.CompaniesViewModelFactory
import com.example.graduationproject.databinding.FragmentCompaniesBinding
import com.example.graduationproject.home.adapter.UsersAdapter
import com.example.graduationproject.home.view.UsersFragmentDirections

import com.example.graduationproject.network.Client

class CompaniesFragment : Fragment() {
    private lateinit var viewModel : CompaniesViewModel
    private lateinit var binding: FragmentCompaniesBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCompaniesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gettingViewModelReady()
        viewModel.getListOfCompanies()
        viewModel.companiesList.observe(requireActivity()){ it->
            val adapter= CompaniesAdapter(it,requireContext())
            binding.rvCompanies.adapter= adapter
            binding.rvCompanies.layoutManager = LinearLayoutManager(requireContext(),  RecyclerView.VERTICAL, false)
          /*  adapter.setOnClickListener(object : UsersAdapter.OnItemClickListener {
                override fun onItemClicked(userData: UserData) {
//                    val  action = UsersFragmentDirections.actionUsersFragmentToChatFragment2(user?.uid.toString(),userData)
                    val  action = UsersFragmentDirections.actionUsersFragmentToChatFragment2(userData)
                    findNavController().navigate(action)
                }
            })*/
        }

    }
    private fun gettingViewModelReady() {
        val companiesViewModelFactory =
            CompaniesViewModelFactory(CompaniesRepoImp(Client))
        viewModel = ViewModelProvider(this, companiesViewModelFactory).get(CompaniesViewModel::class.java)
    }
}