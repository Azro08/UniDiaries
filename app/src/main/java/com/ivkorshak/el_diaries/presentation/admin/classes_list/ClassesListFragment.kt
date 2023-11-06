package com.ivkorshak.el_diaries.presentation.admin.classes_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ivkorshak.el_diaries.R
import com.ivkorshak.el_diaries.data.model.ClassRoom
import com.ivkorshak.el_diaries.databinding.FragmentClassesListBinding
import com.ivkorshak.el_diaries.util.Constants
import com.ivkorshak.el_diaries.util.ScreenState
import kotlinx.coroutines.launch

class ClassesListFragment : Fragment() {
    private var _binding: FragmentClassesListBinding? = null
    private val binding: FragmentClassesListBinding get() = _binding!!
    private val viewModel: ClassesListViewModel by viewModels()
    private var rvAdapter: ClassesListRvAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClassesListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setMenu()
        viewModelOutputs()
    }

    private fun viewModelOutputs() {
        lifecycleScope.launch {
            viewModel.classRooms.collect { state ->
                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Success -> {
                        if (state.data.isNullOrEmpty()) {
                            handleError("No classes found")
                        } else displayClassRooms(state.data)
                    }

                    is ScreenState.Error -> handleError(state.message.toString())
                }
            }
        }
    }

    private fun displayClassRooms(data: List<ClassRoom>) {
        rvAdapter = ClassesListRvAdapter(data, { deleteClassRoom(it) }, { navToClassDetails(it) })
    }

    private fun navToClassDetails(classRoom: ClassRoom) {
        findNavController().navigate(
            R.id.nav_classes_to_add_class,
            bundleOf(Pair(Constants.CLASS_ID, classRoom.id))
        )
    }

    private fun deleteClassRoom(classRoom: ClassRoom) {
        lifecycleScope.launch {
            viewModel.deleteClassRoom(classRoom.id)
            viewModel.classRoomDeleted.collect { state ->
                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Success -> {
                        Toast.makeText(requireContext(), "Classroom deleted", Toast.LENGTH_SHORT)
                            .show()
                    }

                    is ScreenState.Error -> handleError(state.message.toString())
                }
            }
        }
    }

    private fun handleError(errorMsg: String) {
        binding.rvClassesList.visibility = View.GONE
        binding.loadingGif.visibility = View.GONE
        binding.textViewError.visibility = View.VISIBLE
        binding.textViewError.text = errorMsg
    }

    private fun setMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.add_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.itemAdd -> {
                        findNavController().navigate(R.id.nav_classes_to_add_class)
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        rvAdapter = null
    }

}