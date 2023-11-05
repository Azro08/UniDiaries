package com.ivkorshak.el_diaries.data.model

data class MissedClasses(
    val subject : String = "",
    val missed : List<Int> = emptyList()
)
