package com.ivkorshak.el_diaries.data.model

data class Attendance(
    val studentId : String = "",
    val firstName : String = "",
    val lastName : String = "",
    val missedClasses : List<MissedClasses>
)
