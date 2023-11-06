package com.ivkorshak.el_diaries.presentation.admin.add_class

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ivkorshak.el_diaries.data.model.Students
import com.ivkorshak.el_diaries.databinding.FragmentAddClassBinding
import com.ivkorshak.el_diaries.presentation.admin.add_account.StudentsRvAdapter
import com.ivkorshak.el_diaries.util.Constants
import com.ivkorshak.el_diaries.util.ScreenState
import kotlinx.coroutines.launch

class AddClassFragment : Fragment() {
    private var _binding : FragmentAddClassBinding? = null
    private val binding : FragmentAddClassBinding get() = _binding!!
    private var rvAdapter : StudentsRvAdapter? = null
    private val viewModel :  AddClassRoomViewModel by viewModels()
    private val students = arrayListOf<Students>()
    private val studentsInClass = arrayListOf<Students>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddClassBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val classId = arguments?.getString(Constants.CLASS_ID)
        classId?.let {
            loadDetails(it)
        }
        viewModelOutputs()
        binding.buttonSave.setOnClickListener {
            if (areAllFieldsFilled()) saveClass()
            else Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun areAllFieldsFilled(): Boolean = with(binding) {
        val className = editTextClassName.text.toString()
        val classNum = editTextClassRoomNum.text.toString()
        val teacherName = spinnerTeacher.selectedItem.toString()
        if (className.isEmpty() || classNum.isEmpty() || teacherName.isEmpty()
        ) {
            return false
        }
        return true
    }

    private fun saveClass() {

    }

    private fun viewModelOutputs() {
        lifecycleScope.launch { 
            viewModel.students.collect {state ->
                when (state){
                    is ScreenState.Loading -> {}
                    is ScreenState.Error -> Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    is ScreenState.Success -> {
                        if (state.data.isNullOrEmpty()) Toast.makeText(requireContext(), "No students found", Toast.LENGTH_SHORT).show()
                        else{
                            displayStudent(state.data)
                        }
                    }
                }
            }
        }
    }

    private fun displayStudent(data: List<Students>) {
        students.clear()
        students.addAll(data)
        rvAdapter = StudentsRvAdapter(students, {assignStudentToClass(it)}, {removeStudentFromClass(it)})
        binding.rvStudents.setHasFixedSize(true)
        binding.rvStudents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStudents.adapter = rvAdapter
    }

    private fun removeStudentFromClass(student: Students) {
        studentsInClass.remove(student)
    }

    private fun assignStudentToClass(student: Students) {
        studentsInClass.add(student)
    }

    private fun loadDetails(id: String) {

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}