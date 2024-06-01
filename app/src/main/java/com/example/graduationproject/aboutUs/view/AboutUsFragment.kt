package com.example.graduationproject.aboutUs.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.graduationproject.R
import com.example.graduationproject.aboutUs.AboutUsData
import com.example.graduationproject.databinding.FragmentAboutUsBinding
import com.example.graduationproject.databinding.FragmentHomeBinding


class AboutUsFragment : Fragment() {

    private lateinit var  binding :FragmentAboutUsBinding

    private lateinit var aboutUsAdapter: AboutUsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAboutUsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data = mutableListOf<AboutUsData>()
            data.add(AboutUsData("Mona Adel Kamal", R.drawable.mona_photo, "monaadel1812@gmail.com", "https://www.linkedin.com/in/mona-adel-808195228/"))
            data.add(AboutUsData("Rodina Mo'men", R.drawable.rodina_photo, "Rodinamobark3@gmail.com", "https://www.linkedin.com/in/rodina-momen-927b991a5/"))



        aboutUsAdapter = AboutUsAdapter(data)

        binding.rvAboutUs.layoutManager = LinearLayoutManager(activity)
        binding.rvAboutUs.adapter = aboutUsAdapter
    }


}