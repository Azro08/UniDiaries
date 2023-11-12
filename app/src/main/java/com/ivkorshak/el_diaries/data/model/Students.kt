package com.ivkorshak.el_diaries.data.model

data class Students(
    val id : String = "",
    val fullName : String = "",
    val imageUrl : String = "",
    val grades : List<Grade> = listOf(),
    val skippedTime: List<SkippedTime> = listOf()
)
