package com.ivkorshak.el_diaries.presentation.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ivkorshak.el_diaries.R
import com.ivkorshak.el_diaries.databinding.FragmentClassesListBinding

class ClassesListFragment : Fragment() {
    private var _binding : FragmentClassesListBinding? = null
    private val binding : FragmentClassesListBinding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentClassesListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}