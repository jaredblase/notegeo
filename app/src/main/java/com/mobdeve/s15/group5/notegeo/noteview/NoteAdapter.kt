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

class NoteAdapter(
    private val onItemClick: (NoteAndLabel) -> Unit,
    private val onListChanged: (MutableList<NoteAndLabel>) -> Unit
) :
    ListAdapter<NoteAndLabel, NoteAdapter.ViewHolder>(NoteComparator()) {
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
        val item = getItem(position)
        tracker?.let {
            holder.bind(item, it.isSelected(item.note._id))
        }
    }

    override fun onCurrentListChanged(
        previousList: MutableList<NoteAndLabel>,
        currentList: MutableList<NoteAndLabel>
    ) {
        super.onCurrentListChanged(previousList, currentList)
        onListChanged(currentList)
    }

    override fun getItemId(position: Int) = getItem(position).note._id

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