package com.ivkorshak.el_diaries.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ivkorshak.el_diaries.data.model.ClassRoom
import com.ivkorshak.el_diaries.data.model.Students
import com.ivkorshak.el_diaries.util.AuthManager
import com.ivkorshak.el_diaries.util.Constants
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ClassRoomRepository @Inject constructor(
    firestore: FirebaseFirestore,
    private val authManager: AuthManager,
    private val firebaseAuth: FirebaseAuth
) {

    private val classRoomsCollection =
        firestore.collection("classRooms")

    suspend fun getClassRooms(weekDay: Int): List<ClassRoom> {
        val classRoomsList = mutableListOf<ClassRoom>()

        val userRole = authManager.getRole()
        Log.d("getClassRooms", userRole)
        val userId = firebaseAuth.currentUser?.uid

        // Define the Firestore query based on user role
        val query = when (userRole) {
            Constants.ADMIN -> classRoomsCollection // Load all classes
            Constants.TEACHER -> classRoomsCollection.whereEqualTo(
                "teacherId",
                userId
            ) // Load classes assigned to the teacher
            Constants.STUDENT -> classRoomsCollection

            else -> null // Handle other roles or errors as needed
        }
        if (query != null) {
            try {
                val querySnapshot = query.get().await()
                Log.d("snapGetClassRooms", querySnapshot.documents.toString())
                for (document in querySnapshot) {
                    val classRoom = document.toObject(ClassRoom::class.java)
                    if (weekDay == classRoom.dayOfWeek) {
                        classRoomsList.add(classRoom)
                    }
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

    suspend fun getAttachedStudents(classRoomId: String): ArrayList<Students> {
        val classRoom = getClassRoom(classRoomId)
        return classRoom?.students ?: arrayListOf()
    }

}
