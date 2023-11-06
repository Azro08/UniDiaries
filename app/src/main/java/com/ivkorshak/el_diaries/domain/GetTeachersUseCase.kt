package com.ivkorshak.el_diaries.domain

import com.ivkorshak.el_diaries.data.model.Teacher
import com.ivkorshak.el_diaries.data.repository.UsersRepository
import com.ivkorshak.el_diaries.util.ScreenState
import javax.inject.Inject

class GetTeachersUseCase @Inject constructor(
    private val usersRepository: UsersRepository
) {

    suspend operator fun invoke(): ScreenState<List<Teacher>?> {
        val teachers = arrayListOf<Teacher>()
        return try {
            usersRepository.getUsers().forEach { user ->
                if (user.role == "teacher") {
                    val fullName = "${user.firstName} ${user.lastName}"
                    val teacher = Teacher(user.id, fullName, user.imageUrl)
                    teachers.add(teacher)
                }
            }
            ScreenState.Success(teachers)
        } catch (e: Exception) {
            ScreenState.Error(e.message.toString())
        }
    }

}