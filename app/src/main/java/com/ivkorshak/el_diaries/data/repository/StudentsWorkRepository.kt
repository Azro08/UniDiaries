package com.ivkorshak.el_diaries.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.ivkorshak.el_diaries.data.model.ClassRoom
import com.ivkorshak.el_diaries.data.model.Grade
import com.ivkorshak.el_diaries.data.model.SkippedTime
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StudentsWorkRepository @Inject constructor(
    firestore: FirebaseFirestore,
) {

    private val classRoomsCollection =
        firestore.collection("classRooms")

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


}