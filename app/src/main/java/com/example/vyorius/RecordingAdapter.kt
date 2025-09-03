package com.example.vyorius
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vyorius.databinding.ItemRecordingBinding
import java.io.File
class RecordingAdapter(
    private val recordings: List<File>,
    private val onClick: (File) -> Unit
) : RecyclerView.Adapter<RecordingAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemRecordingBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecordingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    override fun getItemCount() = recordings.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = recordings[position]
        holder.binding.fileName.text = file.name
        holder.binding.root.setOnClickListener { onClick(file) }
    }
}
