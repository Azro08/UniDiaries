package com.ivkorshak.el_diaries.presentation.common.calendar

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
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import com.ivkorshak.el_diaries.R
import com.ivkorshak.el_diaries.databinding.FragmentCalendarBinding
import com.ivkorshak.el_diaries.util.Constants
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private var classRoomId = ""
    private val viewModel: CalendarViewModels by viewModels()
    private val events = mutableListOf<Event>()

    companion object {
        private const val ARG_CLASS_ROOM_ID = "arg_class_room_id"

        fun newInstance(classRoomId: String): CalendarFragment {
            val fragment = CalendarFragment()
            val args = Bundle()
            args.putString(ARG_CLASS_ROOM_ID, classRoomId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        classRoomId = requireArguments().getString(ARG_CLASS_ROOM_ID, "")
        setCalendarDate()
    }

    private fun setCalendarDate() {
        binding.compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY)
        val currentDate: Date = Calendar.getInstance().time
        val currentDateString = formatDateWithWeekday(currentDate)
        binding.textViewDate.text = currentDateString
        getNotes()

        binding.compactCalendarView.setListener(object :
            CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date) {
                val formattedDate = formatDateWithWeekday(dateClicked)
                binding.textViewDate.text = formattedDate
                Log.d("TAG", "Day was clicked: $formattedDate")
                val fullDate = Constants.getFullDateString(dateClicked)
                navToNotes(fullDate)
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date) {
                val formattedMonth = formatDateWithWeekday(firstDayOfNewMonth)
                binding.textViewDate.text = formattedMonth
                Log.d("TAG", "Month was scrolled to: $formattedMonth")
            }
        })

    }

    private fun setEvents() {
        binding.compactCalendarView.removeAllEvents()
        binding.compactCalendarView.addEvents(events)
    }

    private fun getNotes() {
        events.clear()
        lifecycleScope.launch {
            viewModel.getNotes(classRoomId)
            viewModel.notes.collect { state ->
                when (state) {
                    is ScreenState.Loading -> {
                        //loading....
                    }

                    is ScreenState.Error -> {
                        //show nothing
                    }

                    is ScreenState.Success -> {
                        state.data!!.forEach {
                            events.add(Event(R.color.darkBlue, Constants.dateStringToLong(it.date)))
                        }
                        setEvents()
                    }
                }
            }
        }
    }

    private fun navToNotes(fullDate: String) {
        Log.d("navNotes", "navToNotes: $fullDate")
        val bundle =
            bundleOf(Pair(Constants.DATE_KEY, fullDate), Pair(Constants.CLASS_ID, classRoomId))
        findNavController().navigate(R.id.nav_classRoom_cal_details, bundle)
    }


    private fun formatDateWithWeekday(date: Date): String {
        val dateFormat = SimpleDateFormat("MMMM d", Locale.getDefault())
        return dateFormat.format(date)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}