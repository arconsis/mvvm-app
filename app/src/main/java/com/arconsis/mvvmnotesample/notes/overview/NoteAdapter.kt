package com.arconsis.mvvmnotesample.notes.overview

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arconsis.mvvmnotesample.data.NoteDto
import com.arconsis.mvvmnotesample.databinding.NoteItemBinding

/**
 * Created by Alexander on 05.05.2017.
 */
class NoteAdapter(private var notes: MutableList<com.arconsis.mvvmnotesample.data.NoteDto> = mutableListOf()) : android.support.v7.widget.RecyclerView.Adapter<NoteViewHolder>() {
    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): com.arconsis.mvvmnotesample.notes.overview.NoteViewHolder {
        val binding = com.arconsis.mvvmnotesample.databinding.NoteItemBinding.inflate(android.view.LayoutInflater.from(parent.context), parent, false)
        return com.arconsis.mvvmnotesample.notes.overview.NoteViewHolder(binding.root, binding)
    }

    override fun onBindViewHolder(holder: com.arconsis.mvvmnotesample.notes.overview.NoteViewHolder, position: Int) {
        holder.binding.note = notes[position]
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    fun updateNotes(notes: MutableList<com.arconsis.mvvmnotesample.data.NoteDto>) {
        this.notes = notes
        notifyDataSetChanged()
    }

    fun addNewNote(note: com.arconsis.mvvmnotesample.data.NoteDto) {
        notes.add(note)
        notifyItemInserted(notes.size - 1)
    }
}

class NoteViewHolder(view: android.view.View, val binding: com.arconsis.mvvmnotesample.databinding.NoteItemBinding) : android.support.v7.widget.RecyclerView.ViewHolder(view)