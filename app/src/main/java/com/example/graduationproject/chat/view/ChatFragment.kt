package com.example.graduationproject.chat.view

import android.content.Context
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.graduationproject.AppVMFactory
import com.example.graduationproject.R
import com.example.graduationproject.Utils
import com.example.graduationproject.chat.model.Message
import com.example.graduationproject.chat.viewModel.ChatViewModel
import com.example.graduationproject.databinding.FragmentChatBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.theartofdev.edmodo.cropper.CropImage

class ChatFragment : Fragment(), onImageMessageClicked {

    private val TAG = "ChatFragment"

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    val args: ChatFragmentArgs by navArgs()

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var chatViewModel: ChatViewModel
    private  lateinit var firestore: FirebaseFirestore

    // for images
    private lateinit var cropImageLauncher: ActivityResultLauncher<Any?>

    private val cropActivityContract = object : ActivityResultContract<Any?, Uri?>(){
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .getIntent(requireActivity())
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        var view = binding.root

        return view
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
//        chatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
        getChatViewModelReady()

        binding.viewModel = chatViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.tvReceiverNameChat.text = args.UserData.userName
        Glide.with(requireContext()).load(args.UserData.imageUri)
            .placeholder(R.drawable.ic_profile)
            .into(binding.imgProfileReceiverChat)

        firestore.collection("Users").document(Utils.getUidLoggedIn()).get().addOnSuccessListener {
            if (it.exists()){
                binding.tvSenderNameChat.text = it.getString("userName")

                Glide.with(requireContext()).load(it.getString("imageUri"))
                    .placeholder(R.drawable.ic_profile)
                    .into(binding.imgProfileSenderChat)
            }else{
                Log.d(TAG, "getting the logged in user's data: the document can't be found")
            }
        }

        binding.iconMessageChat.setOnClickListener {
            chatViewModel.sendMessage(
                Utils.getUidLoggedIn(),
                args.UserData.userId!!,
                args.UserData.userName,
                args.UserData.imageUri
            )
        }

        chatViewModel.getMessages(args.UserData.userId!!).observe(viewLifecycleOwner, Observer {
            initRecyclerView(it)

        })


        cropImageLauncher = registerForActivityResult(cropActivityContract){
            it?.let { uri ->
//                binding.btnUploadImgRegister.setImageURI(uri)

                // image displayed in message

                val source = ImageDecoder.createSource(requireActivity().contentResolver, uri)
                val bitmap = ImageDecoder.decodeBitmap(source)
                chatViewModel.uploadImageToFirebaseStorage(bitmap, args.UserData.userId){imageUploaded->
                    chatViewModel.sendMedia(Utils.getUidLoggedIn() , args.UserData.userId, args.UserData.userName, args.UserData.imageUri, imageUploaded)

                }
            }
        }

        binding.iconCameraChat.setOnClickListener{
            cropImageLauncher.launch(null)
        }


    }



    private fun initRecyclerView(messages: List<Message>) {
        messageAdapter = MessageAdapter(requireContext())
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerViewMessagesChat.layoutManager = layoutManager
        layoutManager.stackFromEnd = true
        messageAdapter.setMessageList(messages)
        messageAdapter.notifyDataSetChanged()
        binding.recyclerViewMessagesChat.adapter = messageAdapter
        messageAdapter.setOnImageMessageListener(this)


    }

    private fun getChatViewModelReady(){
        val factory = AppVMFactory(requireContext())  
        chatViewModel = ViewModelProvider(requireActivity(), factory).get(ChatViewModel::class.java)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun getOnImageMessageClicked(position: Int, messages: Message) {
        if (messages.image.equals("true")){
            val action = ChatFragmentDirections.actionChatFragmentToDisplayImageFragment(messages.message!!)
            view?.findNavController()?.navigate(action)
        }
        else{
            Log.d(TAG, "getOnImageMessageClicked: a text message is clicked....")
        }
        
    }
}