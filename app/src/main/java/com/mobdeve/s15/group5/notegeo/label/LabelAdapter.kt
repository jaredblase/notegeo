package com.mobdeve.s15.group5.notegeo.label

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ObservableArrayList
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.group5.notegeo.databinding.LabelItemBinding
import com.mobdeve.s15.group5.notegeo.models.LabelEntry

class LabelAdapter(private val data: ObservableArrayList<LabelEntry>) : RecyclerView.Adapter<LabelAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LabelItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(data[position])

    override fun getItemCount() = data.size

    inner class ViewHolder(private val binding: LabelItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LabelEntry) {
            binding.item = item
            binding.executePendingBindings()
        }
    }
}