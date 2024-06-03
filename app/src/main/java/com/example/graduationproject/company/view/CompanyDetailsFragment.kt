package com.example.graduationproject.company.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.graduationproject.databinding.FragmentCompanyDetailsBinding


class CompanyDetailsFragment : Fragment() {

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


}