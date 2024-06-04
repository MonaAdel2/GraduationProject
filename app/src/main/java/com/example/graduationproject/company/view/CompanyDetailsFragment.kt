package com.example.graduationproject.company.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.graduationproject.databinding.FragmentCompanyDetailsBinding


class CompanyDetailsFragment : Fragment() {
    private val navArgs: CompanyDetailsFragmentArgs by navArgs()
    private val TAG = "CompanyDetailsFragment"

    private var _binding: FragmentCompanyDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCompanyDetailsBinding.inflate(inflater, container, false)
        var view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvCompanyNameDetails.text=navArgs.companyData.Name
        if(navArgs.companyData.About.isNullOrEmpty()){
            binding.tvAbout.visibility = View.INVISIBLE
        }
        binding.tvCompanyAboutDetails.text=navArgs.companyData.About
        binding.tvCompanyTelephoneDetails.text=navArgs.companyData.Telephones
        binding.tvCompanyAddressDetails.text=navArgs.companyData.Address
        binding.tvCompanyClassficationDetails.text=navArgs.companyData.Classification
    }


}