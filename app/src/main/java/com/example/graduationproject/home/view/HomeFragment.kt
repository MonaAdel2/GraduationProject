package com.example.graduationproject.home.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.graduationproject.authentication.signup.model.UserData
import com.example.graduationproject.databinding.FragmentHomeBinding
import com.example.graduationproject.home.adapter.RecentChatsAdapter
import com.example.graduationproject.home.adapter.onRecentChatClicked
import com.example.graduationproject.home.model.RecentChat
import com.example.graduationproject.home.viewmodel.RecentChatsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject


class HomeFragment : Fragment(), onRecentChatClicked {
    private val TAG = "HomeFragment"
     private lateinit var userBtn: FloatingActionButton
    private lateinit var  binding : FragmentHomeBinding
    private lateinit var recordBtn: FloatingActionButton

    private lateinit var recentChatViewModel: RecentChatsViewModel
    private lateinit var recentChatsAdapter: RecentChatsAdapter
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recentChatViewModel = ViewModelProvider(this).get(RecentChatsViewModel::class.java)

        firestore = FirebaseFirestore.getInstance()

        recentChatsAdapter = RecentChatsAdapter()

        recentChatViewModel.getRecentChat().observe(viewLifecycleOwner, Observer{
            binding.rvRecentChats.layoutManager = LinearLayoutManager(activity)
            recentChatsAdapter.setRecentChatList(it)
            binding.rvRecentChats.adapter = recentChatsAdapter
            checkRecentChatsNumber(it)

        })
        recentChatsAdapter.setOnRecentChatsListener(this)

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

    private fun checkRecentChatsNumber(chatsList: List<RecentChat>) {
        if (chatsList.isEmpty()) {
            binding.tvNoRecentChats.visibility = View.VISIBLE
        } else {
            binding.tvNoRecentChats.visibility = View.GONE
        }
    }

    override fun getOnRecentChatClicked(position: Int, recentChatsList: RecentChat) {

    
           firestore.collection("Users").document(recentChatsList.friendId!!).get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    Log.d(TAG, "getOnRecentChatClicked: ${documentSnapshot.data}")
                    val userData = documentSnapshot.toObject(UserData::class.java)

                    // Now userData contains the retrieved user data
                    if (userData != null) {
                        // Proceed with navigation or any other action
                        val action = HomeFragmentDirections.actionHomeFragmentToChatFragment(userData)
                        view?.findNavController()?.navigate(action)
                    }else{
                        Log.d(TAG, "getOnRecentChatClicked: the data of the user is not found")
                    }
                } else {
                    // Handle case when document doesn't exist
                }
            }.addOnFailureListener { exception ->
                // Handle failure to retrieve document
                Log.e(TAG, "Error retrieving user data: $exception")
            }



    }
}