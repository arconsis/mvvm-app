package com.arconsis.mvvmnotesample.notes

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arconsis.mvvmnotesample.data.NoteDto
import com.arconsis.mvvmnotesample.databinding.NoteItemBinding

/**
 * Created by Alexander on 05.05.2017.
 */
class NoteAdapter(private var notes: MutableList<NoteDto> = mutableListOf()) : RecyclerView.Adapter<NoteViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding.root, binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.binding.note = notes[position]
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    fun updateNotes(notes: MutableList<NoteDto>) {
        this.notes = notes
        notifyDataSetChanged()
    }

    fun addNewNote(note: NoteDto) {
        notes.add(note)
        notifyItemInserted(notes.size - 1)
    }
}

class NoteViewHolder(view: View, val binding: NoteItemBinding) : RecyclerView.ViewHolder(view)