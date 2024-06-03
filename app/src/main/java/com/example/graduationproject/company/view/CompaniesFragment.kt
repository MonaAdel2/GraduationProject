package com.example.graduationproject.company

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.R
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.graduationproject.company.adapter.CompaniesAdapter
import com.example.graduationproject.company.model.CompanyData
import com.example.graduationproject.company.repo.CompaniesRepoImp
import com.example.graduationproject.company.viewmodel.CompaniesViewModel
import com.example.graduationproject.company.viewmodel.CompaniesViewModelFactory
import com.example.graduationproject.databinding.FragmentCompaniesBinding

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
        var companiesSv= binding.svCompanies
        val searchAutoComplete = companiesSv.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchAutoComplete.setTextColor(Color.parseColor("#FFFFFF"))
        searchAutoComplete.setHintTextColor(Color.parseColor("#FFFFFF"))
        viewModel.getListOfCompanies()
        binding.btnSearchByRecord.setOnClickListener {
            var action = CompaniesFragmentDirections.actionCompaniesFragmentToCompaniesSearchRecorderFragment()
            findNavController().navigate(action)
        }


        viewModel.companiesList.observe(requireActivity()){ it->
            if (it != null) {
                val adapter = CompaniesAdapter(it, requireContext())
                binding.rvCompanies.adapter = adapter
                binding.rvCompanies.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                adapter.setOnClickListener(object : CompaniesAdapter.OnItemClickListener {
                    override fun onItemClicked(companyData: CompanyData) {
                        Log.d("TAG", "onViewCreated: $companyData")
                       val action = CompaniesFragmentDirections.actionCompaniesFragmentToCompanyDetailsFragment3(companyData)
                      findNavController().navigate(action)

                    }

                })
            } else {
                Log.e("TAG", "Company list is null")
            }

        }
        companiesSv.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                Toast.makeText(requireContext(), "$query", Toast.LENGTH_SHORT).show()
                viewModel.getCompanyWithText(query)
                return true
            }
            override fun onQueryTextChange(newText: String): Boolean {
                if(newText==""){
                    viewModel.getListOfCompanies()
                }
                return true
            }
        })
    }
    private fun gettingViewModelReady() {
        val companiesViewModelFactory =
            CompaniesViewModelFactory(CompaniesRepoImp(Client))
        viewModel = ViewModelProvider(this, companiesViewModelFactory).get(CompaniesViewModel::class.java)
    }
}