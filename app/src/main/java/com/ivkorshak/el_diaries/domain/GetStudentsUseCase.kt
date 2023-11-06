package com.ivkorshak.el_diaries.domain

import com.ivkorshak.el_diaries.data.model.Students
import com.ivkorshak.el_diaries.data.repository.UsersRepository
import com.ivkorshak.el_diaries.util.ScreenState
import javax.inject.Inject

class GetStudentsUseCase @Inject constructor(
    private val usersRepository: UsersRepository
) {
    suspend operator fun invoke(): ScreenState<List<Students>?> {
        val students = arrayListOf<Students>()
        return try {
            usersRepository.getUsers().forEach { user ->
                if (user.role == "student") {
                    val fullName = "${user.firstName} ${user.lastName}"
                    val student = Students(user.id, fullName, user.imageUrl)
                    students.add(student)
                }
            }
            ScreenState.Success(students)
        } catch (e: Exception) {
            ScreenState.Error(e.message.toString())
        }
    }

}