package com.example.graduationproject.home.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.graduationproject.R
import com.example.graduationproject.Utils
import com.example.graduationproject.authentication.AuthActivity
import com.example.graduationproject.authentication.signup.model.UserData
import com.example.graduationproject.databinding.FragmentHomeBinding
import com.example.graduationproject.home.adapter.RecentChatsAdapter
import com.example.graduationproject.home.adapter.onRecentChatClicked
import com.example.graduationproject.home.model.RecentChat
import com.example.graduationproject.home.viewmodel.RecentChatsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class HomeFragment : Fragment(), onRecentChatClicked {
    private val TAG = "HomeFragment"
    private lateinit var userBtn: FloatingActionButton
    private lateinit var binding: FragmentHomeBinding
    private lateinit var recentChatViewModel: RecentChatsViewModel
    private lateinit var recentChatsAdapter: RecentChatsAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var navigationView: NavigationView? = null
    private var drawerLayout: DrawerLayout? = null
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
        auth = FirebaseAuth.getInstance()

        recentChatsAdapter = RecentChatsAdapter()
        initializeDrawerLayout()
        recentChatViewModel.getRecentChat().observe(viewLifecycleOwner, Observer {
            binding.rvRecentChats.layoutManager = LinearLayoutManager(activity)
            recentChatsAdapter.setRecentChatList(it)
            binding.rvRecentChats.adapter = recentChatsAdapter
            checkRecentChatsNumber(it)

        })
        recentChatsAdapter.setOnRecentChatsListener(this)

        userBtn = binding.btnShowUsers
        userBtn.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToUsersFragment()
            findNavController().navigate(action)
        }
        binding.dotsMenuHome.setOnClickListener {
            showPopupMenu(it)
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


        firestore.collection("Users").document(recentChatsList.friendId!!).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    Log.d(TAG, "getOnRecentChatClicked: ${documentSnapshot.data}")
                    val userData = documentSnapshot.toObject(UserData::class.java)

                    // Now userData contains the retrieved user data
                    if (userData != null) {
                        // Proceed with navigation or any other action
                        val action =
                            HomeFragmentDirections.actionHomeFragmentToChatFragment(userData)
                        view?.findNavController()?.navigate(action)
                    } else {
                        Log.d(TAG, "getOnRecentChatC licked: the data of the user is not found")
                    }
                } else {
                    // Handle case when document doesn't exist
                }
            }.addOnFailureListener { exception ->
            // Handle failure to retrieve document
            Log.e(TAG, "Error retrieving user data: $exception")
        }


    }

    private fun showPopupMenu(view: android.view.View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.action_view_profile -> {
                    Toast.makeText(requireContext(), "view profile", Toast.LENGTH_SHORT).show()
                    goToProfile()
                    true
                }

                R.id.action_about_us -> {
                    Toast.makeText(requireContext(), "about us", Toast.LENGTH_SHORT).show()
                    goToAboutUs()
                    true
                }

                R.id.action_logout -> {
                    Toast.makeText(requireContext(), "logout ", Toast.LENGTH_SHORT).show()
                    logout()
                    true
                }

                R.id.action_companies -> {
                    goToCompanies()
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }

    private fun logout() {
        auth.signOut()
        requireActivity().startActivity(Intent(requireContext(), AuthActivity::class.java))
        requireActivity().finish()
    }

    private fun goToProfile() {
        firestore.collection("Users").document(Utils.getUidLoggedIn()).get().addOnSuccessListener {
            if (it.exists()) {
                val userData = it.toObject(UserData::class.java)
                val action = HomeFragmentDirections.actionHomeFragmentToProfileFragment(userData!!)
                view?.findNavController()?.navigate(action)
            }
        }

    }

    private fun goToCompanies() {
        findNavController().navigate(R.id.action_HomeFragment_to_companiesFragment)
    }

    private fun goToAboutUs() {
        val action = HomeFragmentDirections.actionHomeFragmentToAboutUsFragment()
        view?.findNavController()?.navigate(action)
    }

    private fun initializeDrawerLayout() {
        drawerLayout = binding.drawerLayout
        navigationView = binding.navView
        navigationView!!.bringToFront()
        val toggle =
            ActionBarDrawerToggle(
                this.activity,
                drawerLayout,
                R.string.open_drawer,
                R.string.close_drawer
            )
        drawerLayout!!.addDrawerListener(toggle)
        toggle.syncState()
        navigationView!!.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                com.example.graduationproject.R.id.nav_home -> {
                    goToProfile()
                    // Handle click for nav_home (e.g., show a toast)
                    true
                }

                com.example.graduationproject.R.id.nav_companies -> {
                    goToCompanies()
                    // Handle click for nav_home (e.g., show a toast)
                    true
                }

                com.example.graduationproject.R.id.nav_about_us -> {
                    goToAboutUs()
                    // Handle click for nav_home (e.g., show a toast)
                    true
                }

                com.example.graduationproject.R.id.nav_logout -> {
                    logout()
                    // Handle click for nav_home (e.g., show a toast)
                    true
                }
                // Add other menu items here
                else -> {
                    drawerLayout!!.closeDrawer(GravityCompat.START);
                    false
                }
            }
        }
    }


}





