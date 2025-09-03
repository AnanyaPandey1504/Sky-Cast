package com.example.vyorius
import android.Manifest
import android.app.PictureInPictureParams
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Rational
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.vyorius.databinding.ActivityMainBinding
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var libVLC: LibVLC
    private lateinit var mediaPlayer: MediaPlayer
    private var isRecording = false
    private var currentRtspUrl: String = ""
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(this, "Storage permission is required for recording", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        val options = arrayListOf(
            "--network-caching=300",
            "--rtsp-tcp",
            "--no-drop-late-frames",
            "--no-skip-frames"
        )
        libVLC = LibVLC(this, options)
        mediaPlayer = MediaPlayer(libVLC)
        mediaPlayer.attachViews(binding.videoLayout, null, false, false)
        mediaPlayer.setVideoTrackEnabled(true)
        binding.playButton.setOnClickListener {
            val rtspUrl = binding.urlInput.text.toString().trim()
            if (rtspUrl.isNotEmpty()) {
                currentRtspUrl = rtspUrl
                playStream(rtspUrl)
            } else {
                Toast.makeText(this, "Please enter RTSP URL", Toast.LENGTH_SHORT).show()
            }
        }
        binding.recordingsButton.setOnClickListener {
            val intent = Intent(this, RecordingsActivity::class.java)
            startActivity(intent)
        }

        binding.recordButton.setOnClickListener {
            if (!isRecording) startRecording() else stopRecording()
        }
        binding.pipButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Check if PiP is supported
                if (packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
                    val width = binding.videoLayout.width
                    val height = binding.videoLayout.height
                    if (width > 0 && height > 0) {
                        val aspectRatio = Rational(width, height)
                        val pipParams = PictureInPictureParams.Builder()
                            .setAspectRatio(aspectRatio)
                            .build()
                        enterPictureInPictureMode(pipParams)
                    } else {
                        Toast.makeText(this, "Video layout dimensions are invalid for PiP", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Picture in Picture is not supported on this device", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun playStream(url: String) {
        mediaPlayer.stop()
        val media = Media(libVLC, Uri.parse(url)).apply {
            setHWDecoderEnabled(true, false)
            addOption(":network-caching=300")
            addOption(":rtsp-tcp")
            addOption(":no-audio")
        }
        mediaPlayer.media = media
        media.release()
        mediaPlayer.play()
    }
    private fun startRecording() {
        if (currentRtspUrl.isEmpty()) {
            Toast.makeText(this, "Play stream first", Toast.LENGTH_SHORT).show()
            return
        }
        val folder = File(getExternalFilesDir(null), "recordings")
        if (!folder.exists()) folder.mkdirs()
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val filePath = File(folder, "recording_$timestamp.mp4").absolutePath
        val soutOption = ":sout=#duplicate{dst=display,dst=std{access=file,mux=mp4,dst=\"$filePath\"}}"
        Log.d("Recording", "Saving to: $filePath")
        val media = Media(libVLC, Uri.parse(currentRtspUrl)).apply {
            setHWDecoderEnabled(true, false)
            addOption(":network-caching=300")
            addOption(":rtsp-tcp")
            addOption(":no-audio")
            addOption(soutOption)
        }
        mediaPlayer.stop()
        mediaPlayer.media = media
        media.release()
        mediaPlayer.play()
        isRecording = true
        binding.recordButton.setImageResource(android.R.drawable.ic_media_pause)
        Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show()
    }
    private fun stopRecording() {
        mediaPlayer.stop()
        isRecording = false
        binding.recordButton.setImageResource(R.drawable.record)
        Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show()
        val folder = File(getExternalFilesDir(null), "recordings")
        val files = folder.listFiles()
        Toast.makeText(this, "Saved files: ${files?.size ?: 0}", Toast.LENGTH_SHORT).show()
        files?.forEach {
            Log.d("RecordingCheck", "Saved: ${it.absolutePath}")
        }
        if (currentRtspUrl.isNotEmpty()) {
            playStream(currentRtspUrl)
        }
    }
    override fun onDestroy() {
        mediaPlayer.stop()
        mediaPlayer.release()
        libVLC.release()
        super.onDestroy()
    }
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
                val width = binding.videoLayout.width
                val height = binding.videoLayout.height
                if (width > 0 && height > 0) {
                    val aspectRatio = Rational(width, height)
                    val pipParams = PictureInPictureParams.Builder()
                        .setAspectRatio(aspectRatio)
                        .build()
                    enterPictureInPictureMode(pipParams)
                }
            }
        }
    }
    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode)
        if (isInPictureInPictureMode) {
            binding.playButton.visibility = View.GONE
            binding.urlInput.visibility = View.GONE
            binding.recordButton.visibility = View.GONE
            binding.pipButton.visibility = View.GONE
        } else {
            binding.playButton.visibility = View.VISIBLE
            binding.urlInput.visibility = View.VISIBLE
            binding.recordButton.visibility = View.VISIBLE
            binding.pipButton.visibility = View.VISIBLE
        }
    }
}






