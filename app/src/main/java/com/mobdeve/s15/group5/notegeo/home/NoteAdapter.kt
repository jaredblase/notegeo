package com.mobdeve.s15.group5.notegeo.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.group5.notegeo.databinding.NoteItemBinding
import com.mobdeve.s15.group5.notegeo.models.Note

class NoteAdapter(private val onItemClick: (Note) -> Unit) : ListAdapter<Note, NoteAdapter.ViewHolder>(NoteComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding) { onItemClick(getItem(it)) }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ViewHolder(private val binding: NoteItemBinding, onItemClick: (Int) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener { onItemClick(bindingAdapterPosition) }
        }

        fun bind(note: Note) {
            binding.note = note
            binding.executePendingBindings()
        }
    }

    class NoteComparator : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note) = oldItem === newItem
        override fun areContentsTheSame(oldItem: Note, newItem: Note) = oldItem == newItem
    }
}