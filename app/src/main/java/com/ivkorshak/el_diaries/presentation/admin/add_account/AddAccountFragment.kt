package com.ivkorshak.el_diaries.presentation.admin.add_account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ivkorshak.el_diaries.R
import com.ivkorshak.el_diaries.databinding.FragmentAddAccountBinding

class AddAccountFragment : Fragment() {
    private var _binding : FragmentAddAccountBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddAccountBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    override fun onDestroy() {
        super.onDestroy()
    }

}