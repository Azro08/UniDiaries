package com.ivkorshak.el_diaries.presentation.student.grades

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.ivkorshak.el_diaries.data.model.Grade
import com.ivkorshak.el_diaries.databinding.FragmentStudentsGradeBinding
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StudentsGradeFragment : Fragment() {

    companion object {
        private const val ARG_CLASS_ROOM_ID = "arg_class_room_id"

        fun newInstance(classRoomId: String): StudentsGradeFragment {
            val fragment = StudentsGradeFragment()
            val args = Bundle()
            args.putString(ARG_CLASS_ROOM_ID, classRoomId)
            fragment.arguments = args
            return fragment
        }
    }


    private var _binding: FragmentStudentsGradeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StudentsGradeViewModel by viewModels()
    lateinit var classRoomId: String
    @Inject lateinit var firebaseAuth : FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentsGradeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        classRoomId = requireArguments().getString(ARG_CLASS_ROOM_ID, "")
        val uid = firebaseAuth.currentUser?.uid ?: ""
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh(classRoomId, uid)
            binding.swipeRefreshLayout.isRefreshing = false
        }
        getGrades(uid)

    }

    private fun getGrades(uid: String) {
        lifecycleScope.launch {
            viewModel.getStudentGrades(classRoomId, uid)
            viewModel.studentGrades.collect{ state->
                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Success -> {
                        if (!state.data.isNullOrEmpty()) displayData(state.data)
                        else {
                            handleError("No grades")
                        }
                    }

                    is ScreenState.Error -> {
                        handleError(state.message.toString())
                    }
                }

            }
        }
    }

    private fun handleError(msg: String) {
        binding.textViewGrades.visibility = View.GONE
        binding.tvError.visibility = View.VISIBLE
        binding.tvError.text = msg
    }

    private fun displayData(grades: List<Grade>) {
        binding.tvError.visibility = View.GONE
        binding.textViewGrades.visibility = View.VISIBLE
        val average = "Average: ${grades[0].grades.average()}"
        binding.textViewAverage.text = average
        binding.textViewGrades.text = grades.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}