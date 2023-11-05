package com.ivkorshak.el_diaries.data.model

data class Marks (
    val studentId : String = "",
    val firstName : String = "",
    val lastName : String = "",
    val marks : List<Mark> = emptyList()
)

