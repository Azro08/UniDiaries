package com.ivkorshak.el_diaries.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.ivkorshak.el_diaries.data.model.ClassRoom
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HomeWorkRepository @Inject constructor(
    firestore: FirebaseFirestore,
) {
    private val classRoomsCollection =
        firestore.collection("classRooms")

    suspend fun addHomework(classRoomId: String, homework: String): String {
        return try {
            val classRoomRef = classRoomsCollection.document(classRoomId)

            // Get the current list of homeworks
            val currentHomeworks =
                classRoomRef.get().await().toObject(ClassRoom::class.java)?.homeWorks ?: emptyList()

            // Update the list of homeworks
            val updatedHomeworks = currentHomeworks.toMutableList()
            updatedHomeworks.add(homework)

            // Update the ClassRoom with the modified homeworks list
            classRoomRef.update("homeWorks", updatedHomeworks).await()
            Log.d("addHomework", updatedHomeworks.toString())

            "Done"
        } catch (e: Exception) {
            Log.d("addHomework", e.message.toString())
            e.message.toString()
        }
    }


    suspend fun getHomeworks(classRoomId: String): List<String> {
        return try {
            val classRoomDocument = classRoomsCollection.document(classRoomId).get().await()
            val classRoom = classRoomDocument.toObject(ClassRoom::class.java)
            classRoom?.homeWorks ?: emptyList()
        } catch (e: Exception) {
            Log.d("getHomeworks", e.message.toString())
            emptyList()
        }
    }

    suspend fun deleteHomework(classRoomId: String, homework: String): String {
        return try {
            val classRoomRef = classRoomsCollection.document(classRoomId)

            // Get the current list of homeworks
            val currentHomeworks =
                classRoomRef.get().await().toObject(ClassRoom::class.java)?.homeWorks ?: emptyList()

            // Update the list of homeworks
            val updatedHomeworks = currentHomeworks.toMutableList()
            updatedHomeworks.remove(homework)

            // Update the ClassRoom with the modified homeworks list
            classRoomRef.update("homeWorks", updatedHomeworks).await()
            Log.d("deleteHomework", updatedHomeworks.toString())

            "Done"
        } catch (e: Exception) {
            Log.d("deleteHomework", e.message.toString())
            e.message.toString()
        }
    }


}