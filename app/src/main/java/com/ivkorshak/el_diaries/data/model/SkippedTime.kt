package com.ivkorshak.el_diaries.data.model

data class SkippedTime (
    val classRoomId : String = "",
    val skipped : ArrayList<Int> = arrayListOf()
        )