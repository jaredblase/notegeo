package com.mobdeve.s15.group5.notegeo.noteview

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView

class NoteDetailsLookup(private val rv: RecyclerView) : ItemDetailsLookup<Long>() {
    override fun getItemDetails(e: MotionEvent) =
        rv.findChildViewUnder(e.x, e.y)?.let {
            (rv.getChildViewHolder(it) as NoteAdapter.ViewHolder).getItemDetails()
        }
}