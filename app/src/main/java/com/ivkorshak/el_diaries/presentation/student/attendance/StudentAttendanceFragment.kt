package com.ivkorshak.el_diaries.presentation.student.attendance

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
import com.ivkorshak.el_diaries.data.model.SkippedTimes
import com.ivkorshak.el_diaries.databinding.FragmentStudentAttendanceBinding
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StudentAttendanceFragment : Fragment() {

    companion object {
        private const val ARG_CLASS_ROOM_ID = "arg_class_room_id"

        fun newInstance(classRoomId: String): StudentAttendanceFragment {
            val fragment = StudentAttendanceFragment()
            val args = Bundle()
            args.putString(ARG_CLASS_ROOM_ID, classRoomId)
            fragment.arguments = args
            return fragment
        }
    }


    private var _binding: FragmentStudentAttendanceBinding? = null
    private val binding get() = _binding!!
    lateinit var classRoomId: String
    private val viewModel: StudentAttendanceViewModel by viewModels()

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentAttendanceBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        classRoomId = requireArguments().getString(ARG_CLASS_ROOM_ID, "")
        val uid = firebaseAuth.currentUser?.uid ?: ""
        getAttendance(uid)
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh(classRoomId, uid)
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun getAttendance(uid: String) {
        lifecycleScope.launch {
            viewModel.getSkippedTimes(classRoomId, uid)
            viewModel.skippedTimes.collect { state ->

                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Success -> {
                        if (!state.data.isNullOrEmpty()) displayData(state.data)
                        else {
                            handleError("No skipped classes")
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
        binding.textViewTotalSkipped.visibility = View.GONE
        binding.tvError.visibility = View.VISIBLE
        binding.tvError.text = msg
    }

    private fun displayData(skippedTimes: List<SkippedTimes>) {
        binding.tvError.visibility = View.GONE
        binding.tableLayoutAttendance.visibility = View.VISIBLE
        val groupedSkippedTimes = skippedTimes.flatMap { it.skipped }
            .groupBy { it.date }
            .mapValues { (_, value) ->
                value.joinToString(", ") { "${it.skippedTime}${getString(R.string.h)}" }
            }

        // Iterate through grouped grades and populate the table
        for ((date, gradesText) in groupedSkippedTimes) {
            val row = TableRow(requireContext())
            val averageGrade = skippedTimes.map {
                it.skipped.map { time ->
                    time.skippedTime
                }.sum()
            }
            val totalSkippedTextView =
                "${getString(R.string.total)}: $averageGrade${getString(R.string.h)}"
            val dateTextView = TextView(requireContext())
            val skippedTimeFormatted = "$date $gradesText"
            binding.textViewTotalSkipped.text = totalSkippedTextView
            dateTextView.text = skippedTimeFormatted
            dateTextView.textSize = 20f
            dateTextView.setPadding(10, 10, 10, 10)
            row.addView(dateTextView)

            binding.tableLayoutAttendance.addView(row)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}