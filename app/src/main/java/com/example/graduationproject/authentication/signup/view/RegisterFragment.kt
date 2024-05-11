package com.example.graduationproject.authentication.signup.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.databinding.FragmentRegisterBinding
import androidx.navigation.fragment.navArgs
import com.example.graduationproject.authentication.signup.model.UserData
import com.example.graduationproject.authentication.signup.viewmodel.RegisterFragmentViewModel
import com.google.android.material.textfield.TextInputLayout
import com.theartofdev.edmodo.cropper.CropImage


class RegisterFragment : Fragment() {
    private lateinit var binding : FragmentRegisterBinding
    private val navArgs: RegisterFragmentArgs by navArgs()
    private lateinit var imageUri: Uri
    private val cropActivityContract = object : ActivityResultContract<Any?, Uri?>(){
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity().setAspectRatio(1, 1)
                .getIntent(requireActivity())
        }
        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }
    }
     private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val registerFragmentViewModel= RegisterFragmentViewModel(requireContext())
        binding.btnRegister.setOnClickListener {
            checkNotEmpty(binding.etUserName)
            checkNotEmpty(binding.etPassword)
            checkNotEmpty(binding.etRePassword)
            Toast.makeText(requireContext(), "$imageUri", Toast.LENGTH_SHORT).show()
            registerFragmentViewModel.uploadImageToStorage(imageUri,"${navArgs.phoneNumber}", requireContext())
           val userData = UserData(binding.etUserName.editText?.text.toString(),navArgs.phoneNumber,binding.etPassword.editText?.text.toString(),"images/${navArgs.phoneNumber}$",navArgs.phoneId)
            registerFragmentViewModel.addUser(accessingUser(userData),navArgs.phoneNumber,navArgs.phoneId)
        }
        registerFragmentViewModel.userAdded.observe(requireActivity()){
            if(it==true){
                val action = RegisterFragmentDirections.actionRegisterFragmentToAppNavGraph()
                findNavController().navigate(action)
            }
        }
        cropActivityResultLauncher= registerForActivityResult(cropActivityContract){
            it?.let { uri->
                binding.ivUserPicture.setImageURI(uri)
                imageUri=uri
            }
        }
        binding.ivUserPicture.setOnClickListener {
            cropActivityResultLauncher.launch(null)
        }
    }
    private fun checkNotEmpty(input: TextInputLayout){
        if(input.editText?.text.toString().isEmpty()){
            input.error= "This field is required."
        }else{
            input.error=null
        }
    }
    private fun accessingUser(userData:UserData): HashMap<String,String>{
        val user = hashMapOf(
            "userName" to userData.userName,
            "phoneNumber" to userData.phoneNumber,
            "password" to userData.password,
            "imageUri" to userData.imageUri,
            "userId" to userData.userId
        )
        return user
    }
}