package com.ivkorshak.el_diaries.domain

import com.google.firebase.auth.FirebaseAuth
import com.ivkorshak.el_diaries.data.model.ClassRoom
import com.ivkorshak.el_diaries.data.repository.ClassRoomRepository
import com.ivkorshak.el_diaries.util.AuthManager
import javax.inject.Inject

class GetStudentsClassesUseCase @Inject constructor(
    private val repository: ClassRoomRepository,
    private val firebaseAuth : FirebaseAuth
) {

    suspend operator fun invoke(weekDay : String) : List<ClassRoom>? {
        val studentClasses = mutableListOf<ClassRoom>()
        val uid = firebaseAuth.currentUser?.uid ?: ""
        return try {
            repository.getClassRooms(weekDay).let {
                for (classRoom in it) {
                    for (student in classRoom.students) {
                        if (student.id == uid) {
                            studentClasses.add(classRoom)
                        }
                    }
                }
            }
            studentClasses

        } catch (e: Exception) {
            null
        }
    }

}