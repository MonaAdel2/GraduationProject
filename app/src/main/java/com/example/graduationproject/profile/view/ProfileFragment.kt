package com.example.graduationproject.profile.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.graduationproject.R
import com.example.graduationproject.chat.view.ChatFragmentArgs
import com.example.graduationproject.databinding.FragmentHomeBinding
import com.example.graduationproject.databinding.FragmentProfileBinding
import com.example.graduationproject.profile.viewmodel.ProfileViewModel
import com.google.android.material.textfield.TextInputLayout

class ProfileFragment : Fragment() {
    private lateinit var  binding : FragmentProfileBinding
    private lateinit var profileViewModel: ProfileViewModel
    val args: ProfileFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("MissingInflatedId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel= ProfileViewModel()
        profileViewModel.userNameUpdated.observe(requireActivity()){
            binding.tvAccountUserName.text=it
        }
        binding.tvAccountUserName.text= args.userData.userName
        binding.tvAccountPhoneNumber.text=args.userData.phoneNumber
        Glide.with(requireContext()).load(args.userData.imageUri).into(binding.ivAccountProfilePicture)
        binding.tvChangeAccountName.setOnClickListener {
            val dialog = layoutInflater.inflate(R.layout.change_user_name_dialog, null)
            val myDialog = Dialog(requireContext())
            myDialog.setContentView(dialog)
            myDialog.setCancelable(true)
            myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            myDialog.show()
            val saveBtn = dialog.findViewById<Button>(R.id.btn_save)
            val etChangeUserName= dialog.findViewById<TextInputLayout>(R.id.et_change_user_name)
            val discardBtn = dialog.findViewById<Button>(R.id.btn_discard)
            saveBtn.setOnClickListener {
                if(!checkNotEmpty(etChangeUserName)){
                    profileViewModel.updateUserName(args.userData.userId,etChangeUserName.editText?.text.toString())
                    myDialog.dismiss()
                }
            }
            discardBtn.setOnClickListener {

                myDialog.dismiss()
            }
        }
        binding.tvChangeAccountPassword.setOnClickListener {
            val dialog = layoutInflater.inflate(R.layout.change_password_dialog, null)
            val myDialog = Dialog(requireContext())
            myDialog.setContentView(dialog)
            myDialog.setCancelable(true)
            myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            myDialog.show()
            val saveBtn = dialog.findViewById<Button>(R.id.btn_save)
            val etChangePassword= dialog.findViewById<TextInputLayout>(R.id.et_change_user_password)
            val discardBtn = dialog.findViewById<Button>(R.id.btn_discard)
            saveBtn.setOnClickListener {
                if(!checkNotEmpty(etChangePassword))
                {
                    profileViewModel.updatePassword(args.userData.userId,etChangePassword.editText?.text.toString())
                    myDialog.dismiss()
                }
            }
            discardBtn.setOnClickListener {
                myDialog.dismiss()
            }
        }
        }
    private fun checkNotEmpty(input: TextInputLayout): Boolean{
        if(input.editText?.text.toString().isEmpty()){
            input.error= "This field is required."
            return true
        }else{
            input.error=null
            return false
        }
    }
    }