package com.ivkorshak.el_diaries.presentation.student

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ivkorshak.el_diaries.presentation.common.homework.HomeWorksFragment
import com.ivkorshak.el_diaries.presentation.common.source_list.SourceListFragment
import com.ivkorshak.el_diaries.presentation.student.attendance.StudentAttendanceFragment
import com.ivkorshak.el_diaries.presentation.student.grades.StudentsGradeFragment

class StudentsClassRoomPagerAdapter(fragment: Fragment, private val classRoomID: String) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 4 // Number of pages
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> StudentsGradeFragment.newInstance(classRoomID)
            1 -> StudentAttendanceFragment.newInstance(classRoomID)
            2 -> HomeWorksFragment.newInstance(classRoomID)
            3 -> SourceListFragment.newInstance(classRoomID)
            else -> throw IllegalArgumentException("Invalid position")
        }
    }


}