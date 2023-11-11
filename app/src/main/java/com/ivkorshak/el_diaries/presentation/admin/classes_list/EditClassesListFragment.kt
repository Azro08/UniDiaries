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
import androidx.recyclerview.widget.LinearLayoutManager
import com.ivkorshak.el_diaries.R
import com.ivkorshak.el_diaries.data.model.ClassRoom
import com.ivkorshak.el_diaries.databinding.FragmentEditClassesListBinding
import com.ivkorshak.el_diaries.util.AuthManager
import com.ivkorshak.el_diaries.util.Constants
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EditClassesListFragment : Fragment() {
    private var _binding: FragmentEditClassesListBinding? = null
    private val binding: FragmentEditClassesListBinding get() = _binding!!
    private val viewModel: EditClassesListViewModel by viewModels()
    private var rvAdapter: ClassesListRvAdapter? = null
    @Inject
    lateinit var authManager: AuthManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditClassesListBinding.inflate(layoutInflater)
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
                        binding.loadingGif.visibility = View.GONE
                        binding.rvClassesList.visibility = View.VISIBLE
                        binding.textViewError.visibility = View.GONE
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
        rvAdapter = ClassesListRvAdapter(
            data,
            authManager.getRole(),
            { deleteClassRoom(it) },
            { navToClassDetails(it) })
        binding.rvClassesList.setHasFixedSize(true)
        binding.rvClassesList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvClassesList.adapter = rvAdapter
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