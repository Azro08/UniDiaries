package com.ivkorshak.el_diaries.util

object Constants {
    const val SHARED_PREF_NAME = "user_pref"
    const val USER_KEY = "user_key"
    const val ROLE_KEY = "role_key"
    const val ADMIN = "admin"
    const val TEACHER = "teacher"
    const val STUDENT = "student"
    const val CLASS_ID = "class_id"
    const val STUDENT_ID = "studentId"
    val weekDays =
        listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    val grades = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    val skippedTime = listOf(0, 1, 2, 3, 4)
}