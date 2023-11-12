package com.ivkorshak.el_diaries.presentation.common.classes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ivkorshak.el_diaries.R
import com.ivkorshak.el_diaries.data.model.ClassRoom
import com.ivkorshak.el_diaries.databinding.FragmentClassesListBinding
import com.ivkorshak.el_diaries.presentation.admin.classes_list.ClassesListRvAdapter
import com.ivkorshak.el_diaries.util.AuthManager
import com.ivkorshak.el_diaries.util.Constants
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ClassesListFragment : Fragment() {
    private var _binding: FragmentClassesListBinding? = null
    private val binding get() = _binding!!
    private var rvAdapter: ClassesListRvAdapter? = null
    private var daysListRvAdapter: DaysOfWeekRvAdapter? = null
    private val viewModel: ClassesListViewModel by viewModels()
    @Inject
    lateinit var authManager: AuthManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClassesListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setWeeksDays()
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setWeeksDays() {
        daysListRvAdapter = DaysOfWeekRvAdapter(Constants.weekDays) {
            getClasses(it)
        }
        binding.rvDaysList.setHasFixedSize(true)
        binding.rvDaysList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvDaysList.adapter = daysListRvAdapter
    }

    private fun getClasses(weekDay: String) {
        lifecycleScope.launch {
            if (authManager.getRole() == "teacher") viewModel.getClassRooms(weekDay)
            else viewModel.getStudentsClasses(weekDay)
            viewModel.classRooms.collect { state ->
                when (state) {
                    is ScreenState.Loading -> {
                        binding.loadingGif.visibility = View.VISIBLE
                    }

                    is ScreenState.Success -> {
                        binding.loadingGif.visibility = View.GONE
                        binding.rvClassesList.visibility = View.VISIBLE
                        binding.textViewError.visibility = View.GONE
                        Log.d("Classes", "getClasses: ${state.data?.size}")
                        if (state.data != null) displayClasses(state.data)
                        else handleError("No Classes Available")
                    }

                    is ScreenState.Error -> handleError(state.message.toString())
                }
            }
        }
    }

    private fun displayClasses(classRooms: List<ClassRoom>) {
        rvAdapter =
            ClassesListRvAdapter(classRooms, authManager.getRole(), {}, { navToClassRoom(it.id) })
        binding.rvClassesList.setHasFixedSize(true)
        binding.rvClassesList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvClassesList.adapter = rvAdapter
    }

    private fun navToClassRoom(id: String) {
        if (authManager.getRole() == "teacher") {
            findNavController().navigate(R.id.nav_classes_to_class_room, bundleOf(Pair(Constants.CLASS_ID, id)))
        } else if (authManager.getRole() == "student") {
            findNavController().navigate(R.id.nav_to_student_class_room, bundleOf(Pair(Constants.CLASS_ID, id)))
        }
    }

    private fun handleError(errorMsg: String) {
        binding.rvClassesList.visibility = View.GONE
        binding.loadingGif.visibility = View.GONE
        binding.textViewError.visibility = View.VISIBLE
        binding.textViewError.text = errorMsg
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        rvAdapter = null
    }
}