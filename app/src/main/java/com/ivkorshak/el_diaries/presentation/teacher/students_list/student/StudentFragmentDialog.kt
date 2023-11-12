package com.ivkorshak.el_diaries.presentation.teacher.students_list.student

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ivkorshak.el_diaries.data.model.Grade
import com.ivkorshak.el_diaries.data.model.SkippedTime
import com.ivkorshak.el_diaries.databinding.FragmentStudentDialogBinding
import com.ivkorshak.el_diaries.util.Constants
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StudentFragmentDialog : DialogFragment() {
    private var _binding: FragmentStudentDialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel : EditStudentViewModel by viewModels()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentStudentDialogBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this.activity)
        builder.run { setView(binding.root) }

        val studentId = arguments?.getString(Constants.STUDENT_ID)
        val classId = arguments?.getString(Constants.CLASS_ID)

        val gradesAdapter = ArrayAdapter(
            requireContext(),
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            Constants.grades
        )
        val skippedTimeAdapter = ArrayAdapter(
            requireContext(),
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            Constants.skippedTime
        )
        binding.spinnerGrade.adapter = gradesAdapter
        binding.spinnerSkippedTime.adapter = skippedTimeAdapter

        binding.buttonSave.setOnClickListener {
            setDetails(studentId!!, classId!!)
        }
        binding.buttonDismiss.setOnClickListener { dismiss() }

        return builder.create()
    }

    private fun setDetails(studentId: String, classId: String) {
        lifecycleScope.launch {
            val grade: Int = binding.spinnerGrade.selectedItem.toString().toInt()
            val skipped: Int = binding.spinnerSkippedTime.selectedItem.toString().toInt()
            if (grade != 0 && skipped != 0) {
                try {
                    viewModel.setGradeAndSkippedTime(classId, studentId, Grade(classId, arrayListOf(grade)), SkippedTime(classId, arrayListOf(skipped)))
                    processResponse(ScreenState.Success("Done"))
                } catch (e: Exception) {
                    processResponse(ScreenState.Error(e.message ?: "Unknown error"))
                }
            } else if (grade != 0) {
                val newGrade = Grade(classId, arrayListOf(grade))
                viewModel.setGrade(classId, studentId, newGrade)
                viewModel.gradeIsSet.collect {
                    processResponse(it)
                }
            } else if (skipped != 0) {
                val newSkippedTime = SkippedTime(classId, arrayListOf(skipped))
                viewModel.setSkippedTime(classId, studentId, newSkippedTime)
                viewModel.skippedTimeIsSet.collect {
                    processResponse(it)
                }
            }
        }
    }


    private fun processResponse(state: ScreenState<String?>) {
        when (state) {
            is ScreenState.Loading -> {}
            is ScreenState.Error -> Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
            is ScreenState.Success -> {
                if (state.data != null) {
                    if (state.data == "Done") Toast.makeText(requireContext(), "Saved", Toast.LENGTH_LONG).show()
                    else Toast.makeText(requireContext(), state.data, Toast.LENGTH_LONG).show()
                }
                else Toast.makeText(requireContext(), "Error ${state.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

}