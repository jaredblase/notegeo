package com.mobdeve.s15.group5.notegeo.noteview

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup.ItemDetails
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.group5.notegeo.databinding.NoteItemBinding
import com.mobdeve.s15.group5.notegeo.models.Note

class NoteAdapter(private val onItemClick: (Note) -> Unit) :
    ListAdapter<Note, NoteAdapter.ViewHolder>(NoteComparator()) {
    var tracker: SelectionTracker<Long>? = null
    private lateinit var context: Context

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding = NoteItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding) { onItemClick(getItem(it)) }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = getItem(position)
        tracker?.let {
            holder.bind(note, it.isSelected(note._id))
        }
    }

    override fun getItemId(position: Int) = position.toLong()

    inner class ViewHolder(private val binding: NoteItemBinding, onItemClick: (Int) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener { onItemClick(bindingAdapterPosition) }
        }

        fun bind(mNote: Note, isActivated: Boolean) = binding.run {
            note = mNote
            holderCv.strokeWidth = if (isActivated) 3 else 0
            executePendingBindings()
        }

        fun getItemDetails() = object : ItemDetails<Long>() {
            override fun getPosition() = bindingAdapterPosition
            override fun getSelectionKey() = getItem(bindingAdapterPosition)._id
        }
    }

    class NoteComparator : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note) = oldItem === newItem
        override fun areContentsTheSame(oldItem: Note, newItem: Note) = oldItem == newItem
    }
}