package com.ivkorshak.el_diaries.data.model

data class SkippedTime (
    val classRoomId : String = "",
    val skipped : ArrayList<Int> = arrayListOf(),
        ) {
    override fun toString(): String {
        if (skipped.isEmpty()) {
            return ""
        }

        return skipped.joinToString { "${it}h" }
    }
}