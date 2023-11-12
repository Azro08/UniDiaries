package com.ivkorshak.el_diaries.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.ivkorshak.el_diaries.data.model.ClassRoom
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SourcesRepository @Inject constructor(
    firestore: FirebaseFirestore,
) {

    private val classRoomsCollection =
        firestore.collection("classRooms")

    suspend fun addSource(classRoomId: String, source: String): String {
        return try {
            val classRoomRef = classRoomsCollection.document(classRoomId)

            // Get the current list of homeworks
            val currentHomeworks =
                classRoomRef.get().await().toObject(ClassRoom::class.java)?.sourceList
                    ?: emptyList()

            // Update the list of homeworks
            val updatedHomeworks = currentHomeworks.toMutableList()
            updatedHomeworks.add(source)

            // Update the ClassRoom with the modified homeworks list
            classRoomRef.update("sourceList", updatedHomeworks).await()
            Log.d("addHomework", updatedHomeworks.toString())

            "Done"
        } catch (e: Exception) {
            Log.d("addHomework", e.message.toString())
            e.message.toString()
        }
    }

    suspend fun getSourcesList(classRoomId: String): List<String> {
        return try {
            val classRoomDocument = classRoomsCollection.document(classRoomId).get().await()
            val classRoom = classRoomDocument.toObject(ClassRoom::class.java)
            classRoom?.sourceList ?: emptyList()
        } catch (e: Exception) {
            Log.d("getSources", e.message.toString())
            emptyList()
        }
    }

    suspend fun deleteSource(classRoomId: String, source: String): String {
        return try {
            val classRoomRef = classRoomsCollection.document(classRoomId)

            // Get the current list of homeworks
            val currentHomeworks =
                classRoomRef.get().await().toObject(ClassRoom::class.java)?.sourceList
                    ?: emptyList()

            // Update the list of homeworks
            val updatedHomeworks = currentHomeworks.toMutableList()
            updatedHomeworks.remove(source)

            // Update the ClassRoom with the modified homeworks list
            classRoomRef.update("sourceList", updatedHomeworks).await()
            Log.d("sourceList", updatedHomeworks.toString())

            "Done"
        } catch (e: Exception) {
            Log.d("sourceList", e.message.toString())
            e.message.toString()
        }
    }

}