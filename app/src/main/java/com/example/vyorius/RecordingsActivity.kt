package com.example.vyorius
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vyorius.databinding.ActivityRecordingsBinding
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import java.io.File
class RecordingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecordingsBinding
    private lateinit var libVLC: LibVLC
    private lateinit var mediaPlayer: MediaPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        libVLC = LibVLC(this)
        mediaPlayer = MediaPlayer(libVLC)
        mediaPlayer.attachViews(binding.videoLayout, null, false, false)
        val recordingsDir = File(getExternalFilesDir(null), "recordings")
        val recordings = recordingsDir.listFiles()?.filter {
            it.extension == "mp4" && it.length() > 0
        } ?: emptyList()
        Log.d("RecordingsActivity", "Found files: ${recordings.map { it.name }}")
        binding.recordingsRecycler.layoutManager = LinearLayoutManager(this)
        binding.recordingsRecycler.adapter = RecordingAdapter(recordings) { file ->
            playRecording(file)
        }
    }
    private fun playRecording(file: File) {
        mediaPlayer.stop()
        val media = Media(libVLC, Uri.fromFile(file)).apply {
            setHWDecoderEnabled(true, false)
        }
        mediaPlayer.media = media
        media.release()
        mediaPlayer.play()
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
        libVLC.release()
    }
}

