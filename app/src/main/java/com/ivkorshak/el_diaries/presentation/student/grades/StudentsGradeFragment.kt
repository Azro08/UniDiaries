package com.ivkorshak.el_diaries.presentation.student.grades

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.ivkorshak.el_diaries.R
import com.ivkorshak.el_diaries.data.model.Grades
import com.ivkorshak.el_diaries.databinding.FragmentStudentsGradeBinding
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
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
    @Inject
    lateinit var firebaseAuth: FirebaseAuth
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
            viewModel.studentGrades.collect { state ->
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
        binding.tableLayoutGrades.visibility = View.GONE
        binding.tvError.visibility = View.VISIBLE
        binding.tvError.text = msg
    }

    private fun displayData(grades: List<Grades>) {
        binding.tvError.visibility = View.GONE
        binding.tableLayoutGrades.visibility = View.VISIBLE
        val groupedGrades = grades.flatMap { it.grades }
            .groupBy { it.date }
            .mapValues { (_, value) ->
                value.joinToString(", ") { "${it.grade}/10" }
            }

        // Iterate through grouped grades and populate the table
        for ((date, gradesText) in groupedGrades) {
            val row = TableRow(requireContext())
            val averageGrade = grades.map { it.grades.map {grade->
                grade.grade
            }.average() }
            val averageGradeText = "${getString(R.string.average)}: $averageGrade/10"
            val dateTextView = TextView(requireContext())
            val gradesFormatted = "$date $gradesText"
            binding.textViewAverage.text = averageGradeText
            dateTextView.text = gradesFormatted
            dateTextView.textSize = 20f
            dateTextView.setPadding(10, 10, 10, 10)
            row.addView(dateTextView)

            binding.tableLayoutGrades.addView(row)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}