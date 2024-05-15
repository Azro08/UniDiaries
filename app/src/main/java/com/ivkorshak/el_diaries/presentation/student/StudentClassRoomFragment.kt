package com.ivkorshak.el_diaries.presentation.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.ivkorshak.el_diaries.R
import com.ivkorshak.el_diaries.databinding.FragmentStudentClassRoomBinding
import com.ivkorshak.el_diaries.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudentClassRoomFragment : Fragment() {
    private var _binding: FragmentStudentClassRoomBinding? = null
    private val binding get() = _binding!!
    lateinit var classRoomId: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentClassRoomBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        classRoomId = arguments?.getString(Constants.CLASS_ID) ?: ""
        setViewPager()
    }

    private fun setViewPager() {

        val adapter = StudentsClassRoomPagerAdapter(this, classRoomId)
        binding.classroomViewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.classroomViewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.grades)
                1 -> getString(R.string.attendance)
                2 -> getString(R.string.homework)
                3 -> getString(R.string.sources)
                4 -> getString(R.string.calendar)
                else -> ""
            }
        }.attach()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}