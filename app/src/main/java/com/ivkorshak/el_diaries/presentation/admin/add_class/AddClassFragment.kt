package com.ivkorshak.el_diaries.presentation.admin.add_class

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.R
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ivkorshak.el_diaries.data.model.ClassRoom
import com.ivkorshak.el_diaries.data.model.Students
import com.ivkorshak.el_diaries.data.model.Teacher
import com.ivkorshak.el_diaries.databinding.FragmentAddClassBinding
import com.ivkorshak.el_diaries.presentation.admin.add_account.StudentsRvAdapter
import com.ivkorshak.el_diaries.util.Constants
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddClassFragment : Fragment() {
    private var _binding: FragmentAddClassBinding? = null
    private val binding: FragmentAddClassBinding get() = _binding!!
    private var rvAdapter: StudentsRvAdapter? = null
    private val viewModel: AddClassRoomViewModel by viewModels()
    private val students = arrayListOf<Students>()
    private val spinnerTeachers = arrayListOf("")
    private val teachers = arrayListOf<Teacher>()
    private val studentsInClass = arrayListOf<Students>()
    private var teacherFullName = ""
    private var teacherId = ""

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
        getTeachers()
        binding.buttonSave.setOnClickListener {
            if (areAllFieldsFilled()) saveClass()
            else Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT)
                .show()
        }
        val teachersAdapter = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            spinnerTeachers
        )
        val weekDaysAdapter = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            Constants.weekDays
        )
        binding.spinnerdayOfWeek.adapter = weekDaysAdapter
        binding.spinnerTeacher.adapter = teachersAdapter
    }

    private fun areAllFieldsFilled(): Boolean = with(binding) {
        val className = editTextClassName.text.toString()
        val classNum = editTextClassRoomNum.text.toString()
        val teacherFullName = spinnerTeacher.selectedItem.toString()
        if (className.isEmpty() || classNum.isEmpty() || teacherFullName.isEmpty() ||
            spinnerdayOfWeek.selectedItem.toString().isEmpty()
        ) {
            return false
        }
        return true
    }

    private fun saveClass() {
        setTeacherNameAndId()
        val className = binding.editTextClassName.text.toString()
        val classNumber: Int = binding.editTextClassRoomNum.text.toString().toInt()
        val classId = Constants.generateRandomId()
        val dayOfWeek = binding.spinnerdayOfWeek.selectedItem.toString()
        val classRoom = ClassRoom(
            id = classId,
            className = className,
            roomNum = classNumber,
            teacherId = teacherId,
            teacherFullName = teacherFullName,
            students = studentsInClass,
            dayOfWeek = dayOfWeek
        )
        lifecycleScope.launch {
            viewModel.createClassRoom(classRoom)
            viewModel.classRoomCreated.collect { state ->
                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Error -> Toast.makeText(
                        requireContext(),
                        state.message,
                        Toast.LENGTH_SHORT
                    ).show()

                    is ScreenState.Success -> {
                        Toast.makeText(
                            requireContext(),
                            "Classroom created successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }


    private fun viewModelOutputs() {
        lifecycleScope.launch {
            viewModel.students.collect { state ->
                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Error -> Toast.makeText(
                        requireContext(),
                        state.message,
                        Toast.LENGTH_SHORT
                    ).show()

                    is ScreenState.Success -> {
                        if (state.data.isNullOrEmpty()) Toast.makeText(
                            requireContext(),
                            "No students found",
                            Toast.LENGTH_SHORT
                        ).show()
                        else {
                            displayStudent(state.data)
                        }
                    }
                }
            }
        }
    }

    private fun getTeachers() {
        lifecycleScope.launch {
            viewModel.getTeachers()
            viewModel.teachers.collect { state ->
                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Success -> {
                        spinnerTeachers.clear()
                        teachers.clear()
                        if (state.data.isNullOrEmpty()) Toast.makeText(
                            requireContext(),
                            "No teachers found",
                            Toast.LENGTH_SHORT
                        ).show()
                        else {
                            state.data.forEach {
                                val teacher = "${it.fullName} ${it.id.substring(0, 5)}"
                                spinnerTeachers.add(teacher)
                                teachers.add(it)
                            }
                        }
                    }

                    is ScreenState.Error -> Toast.makeText(
                        requireContext(),
                        state.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun displayStudent(data: List<Students>) {
        students.clear()
        students.addAll(data)
        rvAdapter = StudentsRvAdapter(
            students,
            { assignStudentToClass(it) },
            { removeStudentFromClass(it) })
        binding.rvStudents.setHasFixedSize(true)
        binding.rvStudents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStudents.adapter = rvAdapter
    }

    private fun removeStudentFromClass(student: Students) {
        studentsInClass.remove(student)
        Log.d("studentsInClass", studentsInClass.size.toString())
    }

    private fun assignStudentToClass(student: Students) {
        studentsInClass.add(student)
        Log.d("studentsInClass", studentsInClass.size.toString())
    }

    private fun loadDetails(id: String) {
        lifecycleScope.launch {
            viewModel.getClassRoom(id)
            viewModel.classRoom.collect { state ->
                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Success -> {
                        if (state.data != null) {
                            binding.editTextClassName.setText(state.data.className)
                            binding.editTextClassRoomNum.setText(state.data.roomNum.toString())
                            binding.spinnerdayOfWeek.setSelection(
                                Constants.weekDays.indexOf(state.data.dayOfWeek)
                            )
                            studentsInClass.addAll(state.data.students)
                            displayStudent(studentsInClass)
                        } else Toast.makeText(
                            requireContext(),
                            "No classroom found",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                    is ScreenState.Error -> Toast.makeText(
                        requireContext(),
                        state.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setTeacherNameAndId() {
        teachers.forEach {
            val tempTeacher = "${it.fullName} ${it.id.substring(0, 5)}"
            if (binding.spinnerTeacher.selectedItem.toString() == tempTeacher) {
                teacherFullName = it.fullName
                teacherId = it.id
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}