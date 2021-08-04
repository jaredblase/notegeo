package com.mobdeve.s15.group5.notegeo.label

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.group5.notegeo.databinding.LabelItemBinding
import com.mobdeve.s15.group5.notegeo.models.Label

class LabelAdapter :
    ListAdapter<Label, LabelAdapter.LabelViewHolder>(LabelComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {
        val binding = LabelItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = LabelViewHolder(binding)

        // update text on change
        binding.labelNameEt.doOnTextChanged { text, _, _, _ ->
            getItem(holder.bindingAdapterPosition).label = text.toString()
        }

        return holder
    }

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) =
        holder.bind(getItem(position))

    class LabelViewHolder(private val binding: LabelItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Label) {
            binding.item = item
        }
    }

    class LabelComparator : DiffUtil.ItemCallback<Label>() {
        override fun areItemsTheSame(oldItem: Label, newItem: Label) = oldItem === newItem
        override fun areContentsTheSame(oldItem: Label, newItem: Label) =
            oldItem.label == newItem.label
    }
}