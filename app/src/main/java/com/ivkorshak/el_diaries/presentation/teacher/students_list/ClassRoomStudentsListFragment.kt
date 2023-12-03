package com.ivkorshak.el_diaries.presentation.teacher.students_list

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ivkorshak.el_diaries.R
import com.ivkorshak.el_diaries.data.model.Students
import com.ivkorshak.el_diaries.databinding.FragmentClassRoomStudentsListBinding
import com.ivkorshak.el_diaries.util.Constants
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ClassRoomStudentsListFragment : Fragment() {

    companion object {
        private const val ARG_CLASS_ROOM_ID = "arg_class_room_id"

        fun newInstance(classRoomId: String): ClassRoomStudentsListFragment {
            val fragment = ClassRoomStudentsListFragment()
            val args = Bundle()
            args.putString(ARG_CLASS_ROOM_ID, classRoomId)
            fragment.arguments = args
            return fragment
        }
    }

    private var _binding: FragmentClassRoomStudentsListBinding? = null
    private val binding get() = _binding!!
    private var studentsRvAdapter: StudentsRvAdapter? = null
    private val viewModel: ClassStudentsViewModel by viewModels()
    private lateinit var classRoomId: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClassRoomStudentsListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        classRoomId = requireArguments().getString(ARG_CLASS_ROOM_ID, "")
        binding.swipeRefreshLayout.setOnRefreshListener {
            getStudents(classRoomId)
            binding.swipeRefreshLayout.isRefreshing = false
        }
        getStudents(classRoomId)
        search()
    }

    private fun search() {
        binding.editTextSearchStudent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val searchText = s.toString().trim()
                performSearch(searchText)
            }
        })
    }

    private fun performSearch(query: String) {
        val filteredList = viewModel.filterFoodList(query)
        studentsRvAdapter?.updateFoodList(filteredList)
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
        studentsRvAdapter = StudentsRvAdapter(students) {
            navToStudent(it.id)
        }
        binding.rvStudentsList.setHasFixedSize(true)
        binding.rvStudentsList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStudentsList.adapter = studentsRvAdapter
    }

    private fun navToStudent(id: String) {
        findNavController().navigate(
            R.id.nav_student_to_student_details,
            bundleOf(Pair(Constants.STUDENT_ID, id), Pair(Constants.CLASS_ID, classRoomId))
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        studentsRvAdapter = null
    }

}