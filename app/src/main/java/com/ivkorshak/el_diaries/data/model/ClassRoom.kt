package com.ivkorshak.el_diaries.data.model

data class ClassRoom (
    val id : String = "",
    val className : String = "",
    val roomNum : Int = 0,
    val teacherId : String = "",
    val teacherFullName : String = "",
    val students : List<Users> = listOf(),
    val dayOfWeek : String = ""
        )