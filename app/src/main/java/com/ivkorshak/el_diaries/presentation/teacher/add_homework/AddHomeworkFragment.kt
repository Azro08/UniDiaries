package com.ivkorshak.el_diaries.presentation.teacher.add_homework

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ivkorshak.el_diaries.databinding.FragmentAddHomeworkBinding
import com.ivkorshak.el_diaries.util.Constants
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddHomeworkFragment : DialogFragment() {
    private var _binding: FragmentAddHomeworkBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddHomeWorkViewModel by viewModels()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentAddHomeworkBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this.activity)
        builder.run { setView(binding.root) }

        val classId = arguments?.getString(Constants.CLASS_ID)

        binding.buttonSave.setOnClickListener {
            addHomeWork(classId!!)
        }

        return builder.create()
    }

    private fun addHomeWork(classId: String) {
        val homework = binding.editTextHomeWork.text.toString()
        if (homework.isBlank()) {
            binding.editTextHomeWork.error = "Fill homework"
            return
        } else {
            lifecycleScope.launch {
                viewModel.addHomeWork(classId, homework)
                viewModel.homeWorkAdded.collect { state ->
                    when (state) {
                        is ScreenState.Loading -> {}
                        is ScreenState.Success -> {
                            if (state.data == "Done") dismiss()
                            else Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT)
                                .show()
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
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}