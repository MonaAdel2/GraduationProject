package com.example.graduationproject.chat.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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

class ChatFragment : Fragment() {

    private val TAG = "ChatFragment"

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    val args: ChatFragmentArgs by navArgs()

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var chatViewModel: ChatViewModel
    private  lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        var view = binding.root

        return view
    }

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
    }



    private fun initRecyclerView(messages: List<Message>) {
        messageAdapter = MessageAdapter()
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerViewMessagesChat.layoutManager = layoutManager
        layoutManager.stackFromEnd = true
        messageAdapter.setMessageList(messages)
        messageAdapter.notifyDataSetChanged()
        binding.recyclerViewMessagesChat.adapter = messageAdapter

    }

    private fun getChatViewModelReady(){
        val factory = AppVMFactory(requireContext())  
        chatViewModel = ViewModelProvider(requireActivity(), factory).get(ChatViewModel::class.java)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}