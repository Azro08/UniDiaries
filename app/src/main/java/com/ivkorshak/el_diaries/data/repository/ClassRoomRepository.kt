package com.ivkorshak.el_diaries.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ivkorshak.el_diaries.data.model.ClassRoom
import com.ivkorshak.el_diaries.data.model.Grade
import com.ivkorshak.el_diaries.data.model.SkippedTime
import com.ivkorshak.el_diaries.data.model.Students
import com.ivkorshak.el_diaries.util.AuthManager
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ClassRoomRepository @Inject constructor(
    firestore: FirebaseFirestore,
    private val authManager: AuthManager,
    private val firebaseAuth: FirebaseAuth
) {

    private val classRoomsCollection =
        firestore.collection("classRooms")

    suspend fun getClassRooms(weekDay: String): List<ClassRoom> {
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


    suspend fun setGrade(classRoomId: String, studentId: String, newGrade: Grade): String {
        return try {
            val classRoomRef = classRoomsCollection.document(classRoomId)

            // Get the current list of students
            val currentStudents =
                classRoomRef.get().await().toObject(ClassRoom::class.java)?.students ?: emptyList()

            // Find the student by id
            val updatedStudents = currentStudents.map { student ->
                if (student.id == studentId) {
                    // Check if the classRoomId already exists in grades, if yes, update the list
                    val updatedGrades = student.grades.toMutableList()
                    val existingGrade = updatedGrades.find { it.classRoomId == classRoomId }
                    if (existingGrade != null) {
                        existingGrade.grades.addAll(newGrade.grades)
                    } else {
                        // If the classRoomId doesn't exist, add a new entry
                        updatedGrades.add(newGrade)
                    }

                    // Update the student with the modified grades list
                    student.copy(grades = updatedGrades)
                } else {
                    // For other students, return unchanged
                    student
                }
            }

            // Update the ClassRoom with the modified students list
            classRoomRef.update("students", updatedStudents).await()
            Log.d("setGrade", updatedStudents.toString())

            "Done"
        } catch (e: Exception) {
            Log.d("setGrade", e.message.toString())
            e.message.toString()
        }
    }

    suspend fun setSkippedTime(
        classRoomId: String,
        studentId: String,
        newSkippedTime: SkippedTime
    ): String {
        return try {
            val classRoomRef = classRoomsCollection.document(classRoomId)

            // Get the current list of students
            val currentStudents =
                classRoomRef.get().await().toObject(ClassRoom::class.java)?.students ?: emptyList()

            // Find the student by id
            val updatedStudents = currentStudents.map { student ->
                if (student.id == studentId) {
                    // Check if the classRoomId already exists in skippedTime, if yes, update the list
                    val updatedSkippedTime = student.skippedTime.toMutableList()
                    val existingSkippedTime =
                        updatedSkippedTime.find { it.classRoomId == classRoomId }
                    if (existingSkippedTime != null) {
                        existingSkippedTime.skipped.addAll(newSkippedTime.skipped)
                    } else {
                        // If the classRoomId doesn't exist, add a new entry
                        updatedSkippedTime.add(newSkippedTime)
                    }

                    // Update the student with the modified skippedTime list
                    student.copy(skippedTime = updatedSkippedTime)
                } else {
                    // For other students, return unchanged
                    student
                }
            }

            // Update the ClassRoom with the modified students list
            classRoomRef.update("students", updatedStudents).await()
            Log.d("setGrade", updatedStudents.toString())

            "Done"
        } catch (e: Exception) {
            Log.d("setGrade", e.message.toString())
            e.message.toString()
        }
    }

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
