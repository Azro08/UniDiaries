package com.ivkorshak.el_diaries.presentation.teacher.class_room

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ivkorshak.el_diaries.presentation.teacher.homework.HomeWorksFragment
import com.ivkorshak.el_diaries.presentation.teacher.students_list.ClassRoomStudentsFragment

class ClassRoomPagerAdapter(fragment: Fragment, private val classRoomID : String) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 2 // Number of pages
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ClassRoomStudentsFragment.newInstance(classRoomID)
            1 -> HomeWorksFragment.newInstance(classRoomID)
            else -> throw IllegalArgumentException("Invalid position")
        }
    }


}
