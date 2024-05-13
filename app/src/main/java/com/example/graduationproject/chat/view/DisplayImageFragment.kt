package com.example.graduationproject.chat.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.graduationproject.R
import com.example.graduationproject.databinding.FragmentDisplayImageBinding


class DisplayImageFragment : Fragment() {

    private var _binding: FragmentDisplayImageBinding? = null
    private val binding get() = _binding!!
    private val args: DisplayImageFragmentArgs by navArgs()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDisplayImageBinding.inflate(inflater, container, false)
        var view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(requireContext())
            .load(args.imageUri)
            .placeholder(R.drawable.image_placeholder)
            .into(binding.imageDisplay)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}