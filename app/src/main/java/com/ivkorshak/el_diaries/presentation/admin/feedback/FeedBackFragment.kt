package com.ivkorshak.el_diaries.presentation.admin.feedback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ivkorshak.el_diaries.data.model.FeedBack
import com.ivkorshak.el_diaries.databinding.FragmentFeedBackBinding
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeedBackFragment : Fragment() {
    private var _binding: FragmentFeedBackBinding? = null
    private val binding: FragmentFeedBackBinding get() = _binding!!
    private val viewModel: AdminFeedbackViewModel by viewModels()
    private var rvFeedBackAdapter: FeedbackRvAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBackBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getFeedBack()
    }

    private fun getFeedBack() {
        lifecycleScope.launch {
            viewModel.feedback.collect { state ->
                when (state) {

                    is ScreenState.Loading -> {}
                    is ScreenState.Error -> {
                        handleError(state.message.toString())
                    }

                    is ScreenState.Success -> {
                        if (state.data.isNullOrEmpty()) handleError(state.message.toString())
                        else displayFeedBack(state.data)
                    }
                }
            }
        }
    }

    private fun displayFeedBack(feedBacks: List<FeedBack>) {
        binding.rvAdminFeedbacks.visibility = View.VISIBLE
        binding.textViewError.visibility = View.GONE
        rvFeedBackAdapter = FeedbackRvAdapter(feedBacks)
        binding.rvAdminFeedbacks.setHasFixedSize(true)
        binding.rvAdminFeedbacks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAdminFeedbacks.adapter = rvFeedBackAdapter
    }

    private fun handleError(message: String) {
        binding.rvAdminFeedbacks.visibility = View.GONE
        binding.textViewError.visibility = View.VISIBLE
        binding.textViewError.text = message
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        rvFeedBackAdapter = null
    }

}