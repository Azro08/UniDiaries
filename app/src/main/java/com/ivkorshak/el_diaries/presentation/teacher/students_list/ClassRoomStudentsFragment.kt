package com.ivkorshak.el_diaries.presentation.teacher.students_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ivkorshak.el_diaries.data.model.Students
import com.ivkorshak.el_diaries.databinding.FragmentClassRoomStudentsBinding
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ClassRoomStudentsFragment : Fragment() {

    companion object {
        private const val ARG_CLASS_ROOM_ID = "arg_class_room_id"

        fun newInstance(classRoomId: String): ClassRoomStudentsFragment {
            val fragment = ClassRoomStudentsFragment()
            val args = Bundle()
            args.putString(ARG_CLASS_ROOM_ID, classRoomId)
            fragment.arguments = args
            return fragment
        }
    }

    private var _binding: FragmentClassRoomStudentsBinding? = null
    private val binding get() = _binding!!
    private var studentsRvAdapter: StudentsRvAdapter? = null
    private val viewModel: ClassStudentsViewModel by viewModels()
    private lateinit var classRoomId: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClassRoomStudentsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        classRoomId = requireArguments().getString(ARG_CLASS_ROOM_ID, "")
        binding.swipeRefreshLayout.setOnRefreshListener {
            getStudents(classRoomId)
            binding.swipeRefreshLayout.isRefreshing = false
        }
        getStudents(classRoomId)
    }

    private fun getStudents(roomID: String) {
        lifecycleScope.launch {
            viewModel.getAttachedStudents(roomID)
            viewModel.students.collect { state ->
                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Success -> {
                        binding.rvStudentsList.visibility = View.VISIBLE
                        binding.textViewError.visibility = View.GONE
                        if (state.data != null) displayStudent(state.data)
                        else handleError("No Students found")
                    }

                    is ScreenState.Error -> handleError(state.message.toString())
                }
            }
        }
    }

    private fun handleError(errorMsg: String) {
        binding.rvStudentsList.visibility = View.GONE
        binding.textViewError.visibility = View.VISIBLE
        binding.textViewError.text = errorMsg
    }


    private fun displayStudent(students: List<Students>) {
        studentsRvAdapter = StudentsRvAdapter(students) {}
        binding.rvStudentsList.setHasFixedSize(true)
        binding.rvStudentsList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStudentsList.adapter = studentsRvAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        studentsRvAdapter = null
    }

}