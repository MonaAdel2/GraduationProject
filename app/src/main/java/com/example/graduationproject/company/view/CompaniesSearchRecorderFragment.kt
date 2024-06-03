package com.example.graduationproject.company.view

import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.graduationproject.R
import com.example.graduationproject.company.adapter.CompaniesAdapter
import com.example.graduationproject.company.model.CompanyData
import com.example.graduationproject.company.repo.CompaniesRepoImp
import com.example.graduationproject.company.viewmodel.CompaniesSearchRecorderFactory
import com.example.graduationproject.company.viewmodel.CompaniesSearchRecorderViewModel
import com.example.graduationproject.company.viewmodel.CompaniesViewModel
import com.example.graduationproject.databinding.FragmentCompaniesSearchRecorderBinding
import com.example.graduationproject.home.repo.RecorderRepoImp
import com.example.graduationproject.home.view.RecorderFragmentDirections
import com.example.graduationproject.home.viewmodel.RecorderViewModel
import com.example.graduationproject.home.viewmodel.RecorderViewModelFactory
import com.example.graduationproject.network.Client
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CompaniesSearchRecorderFragment : Fragment() {
    private val permissions = arrayOf(
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private lateinit var binding: FragmentCompaniesSearchRecorderBinding
    private lateinit var mediaRecorder: MediaRecorder
    private var permissionGranted = false
    private var dirPath = ""
    private var fileName = ""
    private var isRecording = false
    private var isPaused = false
    private lateinit var mediaPlayer: MediaPlayer
    private val recordPermissionCode = 111
    private val storage = Firebase.storage
    private val storageReference = storage.reference.child("audio")
    lateinit var viewModel : CompaniesSearchRecorderViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCompaniesSearchRecorderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gettingViewModelReady()
        mediaRecorder = MediaRecorder()
        mediaPlayer = MediaPlayer()
        permissionGranted =
            ActivityCompat.checkSelfPermission(requireContext(), permissions[0]) == PackageManager.PERMISSION_GRANTED
        if (!permissionGranted) {
            ActivityCompat.requestPermissions(requireActivity(), permissions, recordPermissionCode)
        }
        binding.btnSearchRecording.setOnClickListener {
            if (!permissionGranted) {
                ActivityCompat.requestPermissions(requireActivity(), permissions, recordPermissionCode)
                return@setOnClickListener
            }
            when {
                isPaused -> resumeRecording()
                isRecording -> pauseRecorder()
                else -> startRecording()
            }
        }
        binding.btnSearchAboutRecord.setOnClickListener {
            Log.d("SearchAboutRecord", "onViewCreated: searched")
            uploadRecordingToFirebase()
        }
         viewModel.companiesList.observe(requireActivity()){ it->
             binding.tvTranscription.text= it.transcription
           val adapter= CompaniesAdapter(it.result,requireContext())
           binding.rvSearchCompaniesRecord.adapter= adapter
           binding.rvSearchCompaniesRecord.layoutManager = LinearLayoutManager(requireContext(),  RecyclerView.VERTICAL, false)
           adapter.setOnClickListener(object : CompaniesAdapter.OnItemClickListener {
               override fun onItemClicked(companyData: CompanyData) {
                  val action = CompaniesSearchRecorderFragmentDirections.actionCompaniesSearchRecorderFragmentToCompanyDetailsFragment(companyData)
                   findNavController().navigate(action)
               }

           })
       }


    }
    private fun startRecording() {
        if (!permissionGranted) {
            ActivityCompat.requestPermissions(requireActivity(), permissions, recordPermissionCode)
            return
        }

        mediaRecorder = MediaRecorder()
        dirPath = "${requireActivity().externalCacheDir?.absolutePath}/"
        val simpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val date = simpleDateFormat.format(Date())
        fileName = "audio_record_$date"

        mediaRecorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile("$dirPath$fileName.mp4")
            try {
                prepare()
            } catch (e: IOException) {
                Log.e("RecorderFragment", "Error preparing mediaRecorder: ${e.message}")
            }
            start()
            binding.btnSearchRecording.setImageResource(R.drawable.pause_ic)
            isRecording = true
            isPaused = false
            //    binding.btnStoprecording.isEnabled = true
        }
    }

    private fun pauseRecorder() {
        mediaRecorder.pause()
        isPaused = true
        binding.btnSearchRecording.setImageResource(R.drawable.recording_circle_ic)
        stopRecording()
    }

    private fun resumeRecording() {
        mediaRecorder.resume()
        isPaused = false
        binding.btnSearchRecording.setImageResource(R.drawable.pause_ic)
    }

    private fun stopRecording() {
        mediaRecorder.apply {
            stop()
            release()
        }
        isRecording = false
        isPaused = false
        binding.btnSearchRecording.setImageResource(R.drawable.recording_circle_ic)
    }

    private fun uploadRecordingToFirebase() {
        val file = Uri.fromFile(File("$dirPath$fileName.mp4"))
        val audioRef = storageReference.child("$fileName.wav")

        audioRef.putFile(file)
            .addOnSuccessListener {
                audioRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri
                    Log.d("RecorderFragment", "Audio uploaded successfully. URL: $downloadUrl")
                    Log.d("SearchAboutRecord", "onViewCreated: $downloadUrl")
                    viewModel.getCompanyWithAudio(downloadUrl)
                    Log.d("RecorderFragment", "Audio uploaded successfully. URL: $downloadUrl")
                }.addOnFailureListener { e ->
                    // Failed to retrieve download URL
                    Log.e("RecorderFragment", "Error retrieving download URL: ${e.message}")
                }
            }
            .addOnFailureListener { e ->
                // Upload failed
                Log.e("RecorderFragment", "Error uploading audio: ${e.message}")
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == recordPermissionCode && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            binding.btnSearchRecording.isEnabled = true
        }
    }
    private fun gettingViewModelReady() {
        val companiesSearchRecorderFactory =
            CompaniesSearchRecorderFactory(CompaniesRepoImp(Client))
        viewModel = ViewModelProvider(this, companiesSearchRecorderFactory).get(CompaniesSearchRecorderViewModel::class.java)
    }
}