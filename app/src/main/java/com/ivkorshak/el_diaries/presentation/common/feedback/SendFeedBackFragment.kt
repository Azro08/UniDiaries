package com.ivkorshak.el_diaries.presentation.common.feedback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ivkorshak.el_diaries.databinding.FragmentSendFeedBackBinding
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SendFeedBackFragment : Fragment() {
    private var _binding: FragmentSendFeedBackBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SendFeedBackViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSendFeedBackBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.buttonSend.setOnClickListener {
            sendFeedBack()
        }
    }

    private fun sendFeedBack() {
        lifecycleScope.launch {
            val text = binding.editTextFeedBack.text.toString()
            if (text.isNotEmpty()) {
                viewModel.sendFeedBack(text)
                viewModel.feedBackSent.collect { state ->
                    when (state) {
                        is ScreenState.Loading -> {}
                        is ScreenState.Success -> {
                            if (state.data == "Done") {
                                Toast.makeText(requireContext(), "Sent", Toast.LENGTH_SHORT).show()
                                binding.editTextFeedBack.setText("")
                            } else Toast.makeText(
                                requireContext(),
                                state.message,
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
            } else binding.editTextFeedBack.error = "Feedback can't be empty"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}