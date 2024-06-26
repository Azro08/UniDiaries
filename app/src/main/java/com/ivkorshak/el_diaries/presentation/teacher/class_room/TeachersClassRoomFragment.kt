package com.ivkorshak.el_diaries.presentation.teacher.class_room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.ivkorshak.el_diaries.R
import com.ivkorshak.el_diaries.databinding.FragmentTeachersClassRoomBinding
import com.ivkorshak.el_diaries.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeachersClassRoomFragment : Fragment() {
    private var _binding: FragmentTeachersClassRoomBinding? = null
    private val binding get() = _binding!!
    private var classRoomID: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeachersClassRoomBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        classRoomID = arguments?.getString(Constants.CLASS_ID)
        setViewPager()
    }

    private fun setViewPager() {

        val adapter = ClassRoomPagerAdapter(this, classRoomID!!)
        binding.classroomViewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.classroomViewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.students)
                1 -> getString(R.string.homeworks)
                2 -> getString(R.string.sources)
                3 -> getString(R.string.calendar)
                else -> ""
            }
        }.attach()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}