package com.example.graduationproject.chat.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.graduationproject.AppVMFactory
import com.example.graduationproject.Utils
import com.example.graduationproject.chat.model.Message
import com.example.graduationproject.chat.viewModel.ChatViewModel
import com.example.graduationproject.databinding.FragmentChatBinding

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
//    val args: ChatFragmentArgs by navArgs()

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var chatViewModel: ChatViewModel

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

//        chatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
        getChatViewModelReady()

        binding.viewModel = chatViewModel
        binding.lifecycleOwner = viewLifecycleOwner


        binding.iconMessageChat.setOnClickListener {
//            chatViewModel.sendMessage(
//                Utils.getUidLoggedIn(),
//                args.User.userUid!!,
//                args.User.username!!,
//                args.User.imageUrl!!
//            )
        }

//        chatViewModel.getMessages(args.User.userUid!!).observe(viewLifecycleOwner, Observer {
//            initRecyclerView(it)
//        })
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