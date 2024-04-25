package com.example.graduationproject.authentication.signup.view

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import com.example.graduationproject.databinding.FragmentRegisterBinding
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File


class RegisterFragment : Fragment() {
    private lateinit var binding : FragmentRegisterBinding
    private val imageRef = Firebase.storage.reference
    val db = Firebase.firestore
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
        binding.btnRegister.setOnClickListener {
            checkNotEmpty(binding.etUserName)
            checkNotEmpty(binding.etPassword)
            checkNotEmpty(binding.etRePassword)
            uploadImageToStorage("${navArgs.phoneNumber}", requireContext())
            val user = hashMapOf(
                "userName" to binding.etUserName.editText?.text.toString(),
                "phoneNumber" to navArgs.phoneNumber,
                "password" to binding.etPassword.editText?.text.toString(),
                "imageUri" to "images/${navArgs.phoneNumber}$",
                "userId" to navArgs.phoneId
            )
            db.collection("Users").add(user)
                .addOnSuccessListener {
                Toast.makeText(requireContext(), "user added", Toast.LENGTH_SHORT).show()
            }
                .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "failed $e", Toast.LENGTH_SHORT).show()
            }
            val userPhoneNumber = hashMapOf(
                "userPhoneNumber" to navArgs.phoneNumber,
                "userPhoneId" to navArgs.phoneId
            )
            db.collection("PhoneNumbers").add(userPhoneNumber).addOnSuccessListener {
                Toast.makeText(requireContext(), "phone added", Toast.LENGTH_SHORT).show()

            }.addOnFailureListener { e ->
                Toast.makeText(requireContext(), "phone failed $e", Toast.LENGTH_SHORT).show()
            }
        }
        cropActivityResultLauncher= registerForActivityResult(cropActivityContract){
            it?.let { uri->
                binding.ivProfile.setImageURI(uri)
                imageUri=uri
            }
        }
        binding.ivProfile.setOnClickListener {
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
    private fun uploadImageToStorage(fileName:String, context: Context)= CoroutineScope(Dispatchers.IO).launch{
        try {
            imageUri?.let {
                imageRef.child("images/$fileName").putFile(it).await()
            }
        }catch (e:Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}