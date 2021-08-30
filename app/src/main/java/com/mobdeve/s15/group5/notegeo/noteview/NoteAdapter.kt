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
import com.mobdeve.s15.group5.notegeo.models.NoteAndLabel

class NoteAdapter(private val onItemClick: (NoteAndLabel) -> Unit) :
    ListAdapter<NoteAndLabel, NoteAdapter.ViewHolder>(NoteComparator()) {
    var tracker: SelectionTracker<Long>? = null
    private lateinit var context: Context
    private lateinit var data: MutableList<NoteAndLabel>

    init {
        setHasStableIds(true)
    }

    fun modifyList(list: MutableList<NoteAndLabel>) {
        data = list
        submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding = NoteItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding) { onItemClick(getItem(it)) }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        tracker?.let {
            holder.bind(item, it.isSelected(item.note._id))
        }
    }

    override fun getItemId(position: Int) = getItem(position).note._id

    /**
     * Filters the items to display depending if the char sequence received is present in the notes.
     */
    fun filter(query: CharSequence?) {
        if (!query.isNullOrEmpty()) {
            // submit a filtered list. Checks the sequence in the title, body, and label.
            submitList(data.filter {
                it.note.title.contains(query, true) ||
                it.note.body.contains(query, true) ||
                it.label?.name?.contains(query, true) == true
            }.toMutableList())
        } else {
            submitList(data.toCollection(mutableListOf()))  // resubmit the whole list
        }
    }

    inner class ViewHolder(private val binding: NoteItemBinding, onItemClick: (Int) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener { onItemClick(bindingAdapterPosition) }
        }

        fun bind(mNote: NoteAndLabel, isActivated: Boolean) = binding.run {
            item = mNote
            holderCv.strokeWidth = if (isActivated) 3 else 0
            executePendingBindings()
        }

        fun getItemDetails() = object : ItemDetails<Long>() {
            override fun getPosition() = bindingAdapterPosition
            override fun getSelectionKey() = getItem(bindingAdapterPosition).note._id
        }
    }

    class NoteComparator : DiffUtil.ItemCallback<NoteAndLabel>() {
        override fun areItemsTheSame(oldItem: NoteAndLabel, newItem: NoteAndLabel) =
            oldItem.note._id == newItem.note._id

        override fun areContentsTheSame(oldItem: NoteAndLabel, newItem: NoteAndLabel) =
            oldItem == newItem
    }
}