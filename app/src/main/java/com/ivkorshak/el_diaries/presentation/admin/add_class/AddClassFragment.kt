package com.ivkorshak.el_diaries.presentation.admin.add_class

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ivkorshak.el_diaries.R
import com.ivkorshak.el_diaries.databinding.FragmentAddClassBinding

class AddClassFragment : Fragment() {
    private var _binding : FragmentAddClassBinding? = null
    private val binding : FragmentAddClassBinding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddClassBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}