package com.mobdeve.s15.group5.notegeo.label

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.group5.notegeo.NoteGeoApplication
import com.mobdeve.s15.group5.notegeo.databinding.LabelItemBinding
import com.mobdeve.s15.group5.notegeo.focusAndOpenKeyboard
import com.mobdeve.s15.group5.notegeo.hideKeyboard
import com.mobdeve.s15.group5.notegeo.models.Label
import com.mobdeve.s15.group5.notegeo.models.ViewModelFactory


open class LabelAdapter :
    ListAdapter<Label, LabelAdapter.LabelViewHolder>(LabelComparator()) {
    var isSelecting = false
    private lateinit var activity: AppCompatActivity
    private lateinit var model: LabelViewModel

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        activity = recyclerView.context as AppCompatActivity
        model = ViewModelProvider(
            activity,
            ViewModelFactory((activity.applicationContext as NoteGeoApplication).repo)
        ).get(LabelViewModel::class.java)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {
        val binding = LabelItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = LabelViewHolder(binding, isSelecting)

        with(binding) {
            editBtn.setOnClickListener {
                getItem(holder.bindingAdapterPosition).isBeingEdited.set(true)
                activity.focusAndOpenKeyboard(labelNameEt)
            }

            saveBtn.setOnClickListener {
                val item = getItem(holder.bindingAdapterPosition)

                activity.hideKeyboard(it)
                model.updateLabel(item.apply {
                    Log.d("THE TEXT", binding.labelNameEt.text.toString())
                    label = binding.labelNameEt.text.toString()
                })
                item.isBeingEdited.set(false)
            }

            cancelBtn.setOnClickListener {
                val item = getItem(holder.bindingAdapterPosition)

                activity.hideKeyboard(it)
                labelNameEt.setText(item.label)
                item.isBeingEdited.set(false)
            }
        }


        return holder
    }

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) =
        holder.bind(getItem(position))

    class LabelViewHolder(private val binding: LabelItemBinding, private val isSelecting: Boolean) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Label) {
            binding.item = item
            binding.isSelecting = isSelecting
            binding.executePendingBindings()
        }
    }

    class LabelComparator : DiffUtil.ItemCallback<Label>() {
        override fun areItemsTheSame(oldItem: Label, newItem: Label) = oldItem._id == newItem._id
        override fun areContentsTheSame(oldItem: Label, newItem: Label) =
            oldItem.label == newItem.label
    }
}