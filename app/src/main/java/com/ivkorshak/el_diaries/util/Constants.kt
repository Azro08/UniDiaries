package com.ivkorshak.el_diaries.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

object Constants {
    const val DATE_KEY: String = "date_key"
    const val SHARED_PREF_NAME = "user_pref"
    const val USER_KEY = "user_key"
    const val ROLE_KEY = "role_key"
    const val ADMIN = "админ"
    const val TEACHER = "преподаватель"
    const val STUDENT = "студент"
    const val CLASS_ID = "class_id"
    const val STUDENT_ID = "studentId"
    const val LANGUAGE_KEY = "language_key"

    val grades = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    val skippedTime = listOf(0, 1, 2, 3, 4)

    fun generateRandomId(): String {
        val characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val random =
            Random(System.currentTimeMillis()) // Seed the random number generator with the current time

        val randomString = StringBuilder(28)

        for (i in 0 until 28) {
            val randomIndex = random.nextInt(characters.length)
            randomString.append(characters[randomIndex])
        }

        return randomString.toString()
    }

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun getFullDateString(date: Date) : String{
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(date)
    }

    fun dateStringToLong(dateString: String, pattern: String = "yyyy-MM-dd"): Long {
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        val date = formatter.parse(dateString)
        return date?.time ?: 0L
    }


}