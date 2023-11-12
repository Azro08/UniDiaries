package com.ivkorshak.el_diaries.presentation.common.source_list

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ivkorshak.el_diaries.databinding.FragmentSourceListBinding
import com.ivkorshak.el_diaries.util.AuthManager
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SourceListFragment : Fragment() {

    companion object {
        private const val ARG_CLASS_ROOM_ID = "arg_class_room_id"

        fun newInstance(classRoomId: String): SourceListFragment {
            val fragment = SourceListFragment()
            val args = Bundle()
            args.putString(ARG_CLASS_ROOM_ID, classRoomId)
            fragment.arguments = args
            return fragment
        }
    }

    private var _binding: FragmentSourceListBinding? = null
    private val binding get() = _binding!!
    private lateinit var classRoomId: String
    private var sourceRvAdapter: SourcesListRvAdapter? = null
    private val viewModel: SourceListViewModel by viewModels()
    @Inject
    lateinit var authManager : AuthManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSourceListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        classRoomId = requireArguments().getString(ARG_CLASS_ROOM_ID, "")
        if (authManager.getRole() == "student"){
            binding.textViewAddSource.visibility = View.GONE
            binding.button.visibility = View.GONE
        }
        binding.textViewAddSource.setOnClickListener {
            addSource()
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh(classRoomId)
            binding.swipeRefreshLayout.isRefreshing = false
        }
        loadSourceList(classRoomId)
    }

    private fun addSource() {
        binding.editTextContainer.visibility = View.VISIBLE
        binding.editTextSource.visibility = View.VISIBLE
        binding.buttonAddUrl.visibility = View.VISIBLE
        binding.buttonAddUrl.setOnClickListener {
            val source = binding.editTextSource.text.toString()
            if (source.isEmpty()) binding.editTextSource.error = "Source is required"
            else {
                lifecycleScope.launch {
                    viewModel.addSource(classRoomId, source)
                    viewModel.sourceAdded.collect {
                        processResponse(it)
                    }
                }
            }
        }
    }

    private fun loadSourceList(classRoomId: String) {
        lifecycleScope.launch {
            viewModel.getSourceList(classRoomId)
            viewModel.sourceList.collect { state ->
                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Success -> {
                        displaySourcesList(state.data!!)
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

    private fun displaySourcesList(data: List<String>) {
        sourceRvAdapter =
            SourcesListRvAdapter(authManager.getRole(),data, { showConfirmationDialog(it) }, { openLink(it) })
        binding.rvSources.setHasFixedSize(true)
        binding.rvSources.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSources.adapter = sourceRvAdapter
    }

    private fun showConfirmationDialog(source: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Source?")
        builder.setPositiveButton("Yes") { _: DialogInterface?, _: Int ->
            deleteSource(source)
        }
        builder.setNegativeButton(
            "No"
        ) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        builder.show()
    }

    private fun openLink(link: String) {
        val uri = Uri.parse(link)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        requireContext().startActivity(intent)
    }

    private fun deleteSource(source: String) {
        lifecycleScope.launch {
            viewModel.deleteSource(classRoomId, source)
            viewModel.sourceDeleted.collect {
                processResponse(it)
            }
        }
    }

    private fun processResponse(state: ScreenState<String?>) {
        when (state) {
            is ScreenState.Loading -> {}
            is ScreenState.Success -> {
                if (state.data == "Done") {
                    binding.editTextSource.visibility = View.GONE
                    binding.buttonAddUrl.visibility = View.GONE
                    binding.editTextContainer.visibility = View.GONE
                } else Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
            }

            is ScreenState.Error -> Toast.makeText(
                requireContext(),
                state.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        sourceRvAdapter = null
    }

}