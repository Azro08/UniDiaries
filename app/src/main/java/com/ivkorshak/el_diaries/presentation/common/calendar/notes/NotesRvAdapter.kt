package com.ivkorshak.el_diaries.presentation.common.calendar.notes

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ivkorshak.el_diaries.R
import com.ivkorshak.el_diaries.data.model.Notes
import com.ivkorshak.el_diaries.databinding.NoteItemBinding
import com.ivkorshak.el_diaries.util.Constants

class NotesRvAdapter(
    private val notes: List<Notes>,
    private val userRole: String,
    private val deleteListener: (noteId: String) -> Unit,
) : RecyclerView.Adapter<NotesRvAdapter.NotesViewHolder>() {

    class NotesViewHolder(
        deleteListener: (noteId: String) -> Unit,
        private val binding: NoteItemBinding,
        private val userRole: String
    ) : RecyclerView.ViewHolder(binding.root) {
        private var note: Notes? = null
        fun bind(currentNote: Notes) = with(binding) {
            if (userRole == Constants.TEACHER) {
                buttonDelete.visibility = View.VISIBLE
            }
            textViewText.text = currentNote.text
            textViewTitle.text = currentNote.title

            Log.d("NotesRvAdapter", "bind: ${currentNote.priority}")

            when (currentNote.priority) {
                NotePriority.HIGH.name -> mainLayout.setBackgroundResource(R.drawable.rounded_red_background)
                NotePriority.MEDIUM.name -> mainLayout.setBackgroundResource(R.drawable.rounded_green_background)
                NotePriority.LOW.name -> mainLayout.setBackgroundResource(R.drawable.rounded_blue_background)
            }

            note = currentNote
        }

        init {
            binding.buttonDelete.setOnClickListener { deleteListener(note!!.id) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(
            deleteListener,
            NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            userRole = userRole
        )
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.bind(notes[position])
    }

}