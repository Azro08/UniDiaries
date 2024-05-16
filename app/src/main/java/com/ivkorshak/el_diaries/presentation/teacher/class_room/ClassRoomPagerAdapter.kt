package com.ivkorshak.el_diaries.presentation.teacher.class_room

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ivkorshak.el_diaries.presentation.common.calendar.CalendarFragment
import com.ivkorshak.el_diaries.presentation.common.homework.HomeWorksFragment
import com.ivkorshak.el_diaries.presentation.common.source_list.SourceListFragment
import com.ivkorshak.el_diaries.presentation.teacher.students_list.ClassRoomStudentsListFragment

class ClassRoomPagerAdapter(fragment: Fragment, private val classRoomID: String) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 4 // Number of pages
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ClassRoomStudentsListFragment.newInstance(classRoomID)
            1 -> HomeWorksFragment.newInstance(classRoomID)
            2 -> SourceListFragment.newInstance(classRoomID)
            3 -> CalendarFragment.newInstance(classRoomID)
            else -> throw IllegalArgumentException("Invalid position")
        }
    }


}
