package com.ivkorshak.el_diaries.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ivkorshak.el_diaries.data.model.ClassRoom
import com.ivkorshak.el_diaries.util.AuthManager
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ClassRoomRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authManager: AuthManager,
    private val firebaseAuth: FirebaseAuth
) {

    private val classRoomsCollection =
        firestore.collection("classRooms")

    suspend fun getClassRooms(): List<ClassRoom> {
        val classRoomsList = mutableListOf<ClassRoom>()

        val userRole = authManager.getRole()
        val userId = firebaseAuth.currentUser?.uid

        // Define the Firestore query based on user role
        val query = when (userRole) {
            "admin" -> classRoomsCollection // Load all classes
            "teacher" -> classRoomsCollection.whereEqualTo(
                "teacherId",
                userId
            ) // Load classes assigned to the teacher
            "student" -> classRoomsCollection.whereArrayContains(
                "students.id",
                userId!!
            ) // Load classes assigned to the student
            else -> null // Handle other roles or errors as needed
        }

        if (query != null) {
            try {
                val querySnapshot = query.get().await()
                for (document in querySnapshot) {
                    val classRoom = document.toObject(ClassRoom::class.java)
                    classRoomsList.add(classRoom)
                }
            } catch (e: Exception) {
                Log.d("getClassesException", e.message.toString())
            }
        }

        return classRoomsList
    }

    suspend fun deleteClassRoom(classRoomId: String): String {
        return try {
            classRoomsCollection.document(classRoomId).delete().await()
            "Done"
        } catch (e: Exception) {
            e.message.toString()
        }
    }

    suspend fun addClassRoom(classRoom: ClassRoom): String {
        return try {
            classRoomsCollection.document(classRoom.id).set(classRoom).await()
            "Done"
        } catch (e: Exception) {
            e.message.toString()
        }
    }

    suspend fun getClassRoom(classRoomId: String): ClassRoom? {
        return try {
            val document = classRoomsCollection.document(classRoomId).get().await()
            document.toObject(ClassRoom::class.java)
        } catch (e: Exception) {
            e.message.toString()
            null
        }
    }
}
