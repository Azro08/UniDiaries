package com.ivkorshak.el_diaries.data.model

data class Grades (
    val classRoomId : String = "",
    val grades : ArrayList<Grade> = arrayListOf(),
) {
    override fun toString(): String {
        if (grades.isEmpty()) {
            return ""
        }

        return grades.joinToString { "${it}/10" }
    }
}