package com.mobdeve.s15.group5.notegeo.noteview

import androidx.recyclerview.selection.ItemKeyProvider

class NoteKeyProvider(private val adapter: NoteAdapter) : ItemKeyProvider<Long>(SCOPE_CACHED) {
    override fun getKey(position: Int) = adapter.currentList[position].note._id
    override fun getPosition(key: Long) = adapter.currentList.indexOfFirst { it.note._id == key }
}