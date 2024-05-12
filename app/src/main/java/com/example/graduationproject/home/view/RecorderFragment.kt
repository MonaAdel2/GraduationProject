package com.example.graduationproject.home.view

import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.example.graduationproject.R
import com.example.graduationproject.databinding.FragmentRecorderBinding
import com.example.graduationproject.databinding.FragmentRegisterBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton


class RecorderFragment : Fragment() {
    private lateinit var binding: FragmentRecorderBinding
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var mediaPlayer: MediaPlayer
    private val recordPermissionCode = 111
    private lateinit var recordFilePath: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecorderBinding.inflate(inflater, container, false)
        recordFilePath = "${requireActivity().externalCacheDir?.absolutePath}/my-rec.3gp"
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRecording.isEnabled = false
        binding.btnStoprecording.isEnabled = false
        mediaRecorder = MediaRecorder()
        mediaPlayer = MediaPlayer()

        binding.btnRecording.setOnClickListener {
            if(getRecordPermission())
            startRecording()
        }
        binding.btnStoprecording.setOnClickListener {
            stopRecording()
        }

        binding.btnPlayRecord.setOnClickListener {
            playRecording()
        }
    }

    private fun startRecording() {
        try {
            mediaRecorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(recordFilePath)
                prepare()
                start()
            }
            binding.btnRecording.isEnabled = false
            binding.btnStoprecording.isEnabled = true
        } catch (e: Exception) {
            Log.e("RecorderFragment", "Error starting recording: ${e.message}")
        }
    }

    private fun stopRecording() {
        try {
            mediaRecorder.stop()
            mediaRecorder.release()
            binding.btnRecording.isEnabled = true
            binding.btnStoprecording.isEnabled = false
        } catch (e: Exception) {
            Log.e("RecorderFragment", "Error stopping recording: ${e.message}")
        }
    }

    private fun playRecording() {
        try {
            mediaPlayer.apply {
                setDataSource(recordFilePath)
                prepare()
                start()
            }
        } catch (e: Exception) {
            Log.e("RecorderFragment", "Error playing recording: ${e.message}")
        }
    }

    private fun getRecordPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                recordPermissionCode
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == recordPermissionCode && grantResults.isNotEmpty() &&
            grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        ) {
            binding.btnRecording.isEnabled = true
        }
    }
}