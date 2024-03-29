package com.mobdeve.s15.group5.notegeo.label

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.group5.notegeo.*
import com.mobdeve.s15.group5.notegeo.databinding.LabelItemBinding
import com.mobdeve.s15.group5.notegeo.models.Label
import com.mobdeve.s15.group5.notegeo.models.ViewModelFactory
import kotlinx.coroutines.Dispatchers

open class LabelAdapter :
    ListAdapter<Label, LabelAdapter.LabelViewHolder>(LabelComparator()) {
    var isChoosingLabel = false
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var activity: AppCompatActivity
    private lateinit var model: LabelViewModel
    var lastEditedPosition = -1

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        activity = recyclerView.context as AppCompatActivity
        layoutManager = recyclerView.layoutManager!!
        model = ViewModelProvider(
            activity,
            ViewModelFactory((activity.applicationContext as NoteGeoApplication).repo, Dispatchers.Default)
        ).get(LabelViewModel::class.java)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {
        val binding = LabelItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = LabelViewHolder(binding, isChoosingLabel)

        with(binding) {
            editBtn.setOnClickListener {
                // if another edit is occurring
                if (lastEditedPosition != -1) {
                    getItem(lastEditedPosition).isBeingEdited.set(false)

                    layoutManager.findViewByPosition(lastEditedPosition)
                        ?.findViewById<EditText>(R.id.label_name_et)
                        ?.setText(
                            getItem(lastEditedPosition).name
                        )
                }

                getItem(holder.bindingAdapterPosition).isBeingEdited.set(true)
                lastEditedPosition = holder.bindingAdapterPosition
            }

            saveBtn.setOnClickListener {
                val item = getItem(holder.bindingAdapterPosition)

                activity.hideKeyboard(it)
                lastEditedPosition = -1
                model.updateLabel(item.apply { name = binding.labelNameEt.text.toString() })
                item.isBeingEdited.set(false)
            }

            cancelBtn.setOnClickListener {
                val item = getItem(holder.bindingAdapterPosition)

                lastEditedPosition = -1
                activity.hideKeyboard(it)
                labelNameEt.setText(item.name)
                item.isBeingEdited.set(false)
            }

            // onCheckChanged doesn't seem to work. I settled with this one
            labelItemCb.setOnClickListener {
                model.modifyNumSelected(labelItemCb.isChecked)
            }

            labelNameEt.setOnClickListener {
                if (!getItem(holder.bindingAdapterPosition).isBeingEdited.get()) {
                    (if (isChoosingLabel) labelItemRb else labelItemCb).performClick()
                }
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
            oldItem.name == newItem.name
    }
}