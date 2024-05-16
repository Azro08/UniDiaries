package com.ivkorshak.el_diaries.data.repository

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.FirebaseFirestore
import com.ivkorshak.el_diaries.data.model.Notes
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NotesRepository @Inject constructor(
    firestore: FirebaseFirestore
) {

    private val notesRef = firestore.collection("notes")

    suspend fun getNotes(classRoomId: String, date : String): List<Notes> {
        Log.d("NotesRepository", "getNotes:  $date")
        return try {
            if (date.isEmpty()) {
                notesRef.whereEqualTo("classRoomId", classRoomId)
                    .get()
                    .await()
                    .toObjects(Notes::class.java)
            } else {
                notesRef.whereEqualTo("classRoomId", classRoomId)
                    .whereEqualTo("date", date)
                    .get()
                    .await()
                    .toObjects(Notes::class.java)
            }
        } catch (e: FirebaseException) {
            Log.d("NotesException", e.message.toString())
            emptyList()
        }
    }

    suspend fun deleteNote(noteId: String): String {
        return try {
            notesRef.document(noteId).delete().await()
            "Done"
        } catch (e: FirebaseException) {
            Log.d("NotesException", e.message.toString())
            e.message.toString()
        }
    }

    suspend fun addNote(note: Notes): Boolean {
        return try {
            notesRef.document(note.id).set(note).await()
            true
        } catch (e: FirebaseException) {
            Log.d("NotesException", e.message.toString())
            false
        }
    }

}