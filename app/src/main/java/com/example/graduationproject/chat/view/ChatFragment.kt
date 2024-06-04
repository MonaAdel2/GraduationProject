package com.example.graduationproject.chat.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.graduationproject.AppVMFactory
import com.example.graduationproject.R
import com.example.graduationproject.SharedPrefs
import com.example.graduationproject.Utils
import com.example.graduationproject.chat.model.Message
import com.example.graduationproject.chat.viewModel.ChatViewModel
import com.example.graduationproject.databinding.FragmentChatBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.hitomi.cmlibrary.CircleMenu
import com.theartofdev.edmodo.cropper.CropImage

class ChatFragment : Fragment(), onImageMessageClicked {

    private val TAG = "ChatFragment"
    private var clicked = false
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    val args: ChatFragmentArgs by navArgs()
   private val rotateOpen:Animation by lazy { AnimationUtils.loadAnimation(requireContext(),R.anim.rotate_open_anim) }
    private val rotateClose:Animation by lazy { AnimationUtils.loadAnimation(requireContext(),R.anim.rotate_close_anim) }
    private val toButton:Animation by lazy { AnimationUtils.loadAnimation(requireContext(),R.anim.to_button_anim) }
    private val fromButton:Animation by lazy { AnimationUtils.loadAnimation(requireContext(),R.anim.from_button_anim) }
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var chatViewModel: ChatViewModel
    private  lateinit var firestore: FirebaseFirestore


    private lateinit var constraintLayout: ConstraintLayout

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

        constraintLayout = binding.chatLayout
//        binding.circleMenu.setMainMenu(Color.parseColor("#CDCDCD"),R.drawable.menu_icon,R.drawable.cancel_icon)
//            .addSubMenu(Color.parseColor("#88bef5"),R.drawable.camera_icon)
//            .addSubMenu(Color.parseColor("#83e85a"),R.drawable.record_ic)
//            .setOnMenuSelectedListener {index->
//                when (index) {
//                    0 -> {
//                        Toast.makeText(requireActivity(), "Camera", Toast.LENGTH_SHORT).show()
////                        constraintLayout.setBackgroundColor(Color.parseColor("#ecfffb"))
//                    }
//                    1 -> {
//                        Toast.makeText(requireActivity(), "Record", Toast.LENGTH_SHORT).show()
////                        constraintLayout.setBackgroundColor(Color.parseColor("#96f7d2"))
//                    }
//
//                }
//
//
//            }

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

     /*   binding.cardView2.setOnClickListener{
            showPopupMenu(it)

        }*/

        binding.fbChattingOptions.setOnClickListener {
                onChattingOptionsClicked()
        }
        binding.fbSendImage.setOnClickListener {
            Toast.makeText(requireContext(), "Send Imgae", Toast.LENGTH_SHORT).show()
            openGallery()
        }
        binding.fbSpeechToText.setOnClickListener {
            Toast.makeText(requireContext(), "Speech to text", Toast.LENGTH_SHORT).show()
            goToRecording()
        }

    }

    private fun onChattingOptionsClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        clicked = !clicked
    }

    private fun setAnimation(clicked:Boolean) {
        if(!clicked){
            binding.fbSendImage.startAnimation(fromButton)
            binding.fbSpeechToText.startAnimation(fromButton)
            binding.fbChattingOptions.startAnimation(rotateOpen)
        }else{
            binding.fbSendImage.startAnimation(toButton)
            binding.fbSpeechToText.startAnimation(toButton)
            binding.fbChattingOptions.startAnimation(rotateClose)
        }
    }

    private fun setVisibility(clicked:Boolean) {
        if(!clicked){
            binding.fbSendImage.visibility= View.VISIBLE
            binding.fbSpeechToText.visibility=View.VISIBLE
        }else{
            binding.fbSendImage.visibility= View.INVISIBLE
            binding.fbSpeechToText.visibility=View.INVISIBLE
        }
    }
    private fun setClickable(clicked:Boolean){
        if(!clicked){
            binding.fbSendImage.isClickable= false
            binding.fbSpeechToText.isClickable=false
        }else{
            binding.fbSendImage.isClickable= true
            binding.fbSpeechToText.isClickable= true

        }
    }

    /* private fun showPopupMenu(view: android.view.View) {
         val popupMenu = PopupMenu(requireContext(), view)
         popupMenu.menuInflater.inflate(R.menu.chat_options, popupMenu.menu)

         popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
             when (menuItem.itemId) {
                 R.id.action_send_image -> {
                     Toast.makeText(requireContext(), "Send Imgae", Toast.LENGTH_SHORT).show()
                     openGallery()
                     true
                 }
                 R.id.action_speech_to_text -> {
                     Toast.makeText(requireContext(), "Speech to text", Toast.LENGTH_SHORT).show()
                     goToRecording()
                     true
                 }
                 else -> false
             }
         }

         popupMenu.show()
     }*/

    private fun openGallery(){
        cropImageLauncher.launch(null) //******
    }

    private fun goToRecording(){
        var action = ChatFragmentDirections.actionChatFragmentToRecorderFragment(args.UserData)
        findNavController().navigate(action)
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

    override fun onResume() {
        super.onResume()

        val mySharedPrefs = SharedPrefs(requireContext())
        var transcribedMessage = mySharedPrefs.getValue("transcribe")
//        binding.etMessageChat.editText?.text = transcribedMessage
        if(transcribedMessage!=null){
            val editableText: Editable = Editable.Factory.getInstance().newEditable(transcribedMessage)
            binding.etMessageChat.editText?.text = editableText
            mySharedPrefs.setValue("transcribe", "")
        }

    }
}