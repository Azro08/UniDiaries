package com.ivkorshak.el_diaries.presentation.student

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ivkorshak.el_diaries.R
import com.ivkorshak.el_diaries.databinding.ActivityStudentsBinding

class StudentsActivity : AppCompatActivity() {
    private var _binding : ActivityStudentsBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityStudentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}