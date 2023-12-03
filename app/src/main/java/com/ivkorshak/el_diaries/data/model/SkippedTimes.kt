package com.ivkorshak.el_diaries.data.model

data class SkippedTimes (
    val classRoomId : String = "",
    val skipped : ArrayList<SkippedTime> = arrayListOf(),
        ) {
    override fun toString(): String {
        if (skipped.isEmpty()) {
            return ""
        }

        return skipped.joinToString { "${it}h" }
    }
}