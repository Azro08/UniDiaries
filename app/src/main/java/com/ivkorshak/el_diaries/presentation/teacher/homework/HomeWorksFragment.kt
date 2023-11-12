package com.ivkorshak.el_diaries.presentation.teacher.homework

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ivkorshak.el_diaries.R
import com.ivkorshak.el_diaries.databinding.FragmentHomeWorksBinding
import com.ivkorshak.el_diaries.util.Constants
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeWorksFragment : Fragment() {

    companion object {
        private const val ARG_CLASS_ROOM_ID = "arg_class_room_id"

        fun newInstance(classRoomId: String): HomeWorksFragment {
            val fragment = HomeWorksFragment()
            val args = Bundle()
            args.putString(ARG_CLASS_ROOM_ID, classRoomId)
            fragment.arguments = args
            return fragment
        }
    }

    private var _binding: FragmentHomeWorksBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeWorksViewModel by viewModels()
    private var homeWorksRvAdapter: HomeWorksRvAdapter? = null
    private lateinit var classRoomId: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeWorksBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        classRoomId = requireArguments().getString(ARG_CLASS_ROOM_ID, "")
        getHomeWorks(classRoomId)
        binding.textViewAddHomeWork.setOnClickListener{
            findNavController().navigate(R.id.nav_to_add_homework, bundleOf(Pair(Constants.CLASS_ID, classRoomId)))
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh(classRoomId)
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh(classRoomId)
        Log.d("HomeWorksFragment", "onResume")
    }
    private fun getHomeWorks(classRoomId: String) {
        lifecycleScope.launch {
            viewModel.getHomeWorks(classRoomId)
            viewModel.homeWorks.collect { state ->
                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Success -> {
                        if (state.data != null) displayHomeWorks(state.data)
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

    private fun displayHomeWorks(homeWorks: List<String>) {
        homeWorksRvAdapter = HomeWorksRvAdapter(homeWorks) {
            showConfirmationDialog(it)
        }
        binding.rvHomeworks.setHasFixedSize(true)
        binding.rvHomeworks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHomeworks.adapter = homeWorksRvAdapter
    }

    private fun showConfirmationDialog(homeWork: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Homework?")
        builder.setPositiveButton("Yes") { _: DialogInterface?, _: Int ->
            deleteHomeWork(homeWork)
        }
        builder.setNegativeButton(
            "No"
        ) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        builder.show()
    }


    private fun deleteHomeWork(homeWork: String) {
        lifecycleScope.launch {
            viewModel.deleteHomework(classRoomId, homeWork)
            viewModel.homeworkDeleted.collect { state ->
                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Success -> {
                        if (state.data == "Done") Toast.makeText(
                            requireContext(),
                            "Deleted",
                            Toast.LENGTH_SHORT
                        ).show()
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        homeWorksRvAdapter = null
    }
}