package com.ivkorshak.el_diaries.data.model

data class Grade (
    val classRoomId : String = "",
    val grades : ArrayList<Int> = arrayListOf(),
) {
    override fun toString(): String {
        if (grades.isEmpty()) {
            return ""
        }

        return grades.joinToString { "${it}/10" }
    }
}