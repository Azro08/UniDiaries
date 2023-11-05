package com.ivkorshak.el_diaries.util.firestor_service

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreDeleteService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun deleteFromFirestore(collectionName: String, documentId: String): String {
        val userDocument = firestore.collection(collectionName).document(documentId)
        return try {
            userDocument.delete().await()
            "Done"
        } catch (e: Exception) {
            e.message.toString()
        }
    }
}
