package com.ivkorshak.el_diaries.presentation.common.classes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ivkorshak.el_diaries.R
import com.ivkorshak.el_diaries.databinding.DaysItemBinding

class DaysOfWeekRvAdapter(
    private val days: List<Int>,
    private var listener: (dayOfWeek: Int) -> Unit
) : RecyclerView.Adapter<DaysOfWeekRvAdapter.DaysOfWeekViewHolder>() {

    private var lastClickedIndex: Int = -1

    class DaysOfWeekViewHolder(
        private var adapter: DaysOfWeekRvAdapter,
        private var binding: DaysItemBinding,
        private val listener: (dayOfWeek: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        private var day: Int? = null

        fun bind(curDay: Int, position: Int) {
            val context = binding.root.context
            val weekDaysMap = mapOf(
                1 to context.getString(R.string.mon),
                2 to context.getString(R.string.tue),
                3 to context.getString(R.string.wed),
                4 to context.getString(R.string.thu),
                5 to context.getString(R.string.fri),
                6 to context.getString(R.string.sat),
                7 to context.getString(R.string.sun)
            )
            val dayName = weekDaysMap[curDay] ?: ""
            binding.textViewDayOfWeek.text = dayName
            day = curDay

            // Set the background based on `whether` this item was the last clicked one
            if (adapter.lastClickedIndex == position) {
                // Set background for the last clicked item
                binding.root.setBackgroundResource(R.drawable.rounded_blue_background)
                binding.textViewDayOfWeek.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.white
                    )
                )
            } else {
                // Set the default background for other items
                binding.root.setBackgroundResource(R.drawable.rounded_white_background)
                binding.textViewDayOfWeek.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.black
                    )
                )
            }
        }

        init {
            binding.root.setOnClickListener {
                listener(day!!)
                // Update the background for the last clicked item
                if (adapter.lastClickedIndex != -1) {
                    adapter.notifyItemChanged(adapter.lastClickedIndex)
                }

                // Update the background for the currently clicked item
                adapter.lastClickedIndex = adapterPosition
                adapter.notifyItemChanged(adapterPosition)

                // Notify the listener
                adapter.listener(day!!)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaysOfWeekViewHolder {
        return DaysOfWeekViewHolder(
            this,
            DaysItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            listener
        )
    }

    override fun getItemCount(): Int {
        return days.size
    }

    override fun onBindViewHolder(holder: DaysOfWeekViewHolder, position: Int) {
        holder.bind(days[position], position)
    }
}
