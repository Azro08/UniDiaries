package com.ivkorshak.el_diaries.util.firestor_service

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreAddService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun saveToFirestore(doc: Any, collectionName: String, documentId: String): String {
        val usersCollection = firestore.collection(collectionName)
        return try {
            // Firestore-specific logic here
            val userDocRef = usersCollection.document(documentId)
            userDocRef.set(doc).await()
            "Done"
        } catch (e: Exception) {
            e.message.toString()
        }
    }
}