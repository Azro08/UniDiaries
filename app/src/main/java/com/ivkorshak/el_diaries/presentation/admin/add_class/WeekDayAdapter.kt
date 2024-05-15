package com.ivkorshak.el_diaries.presentation.admin.add_class

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.ivkorshak.el_diaries.R

class WeekDayAdapter(context: Context, weekDays: Array<Int>) :
    ArrayAdapter<Int>(context, android.R.layout.simple_spinner_item, weekDays) {

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        (view.findViewById<View>(android.R.id.text1) as TextView).text =
            getWeekDayName(position + 1)
        return view
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        (view.findViewById<View>(android.R.id.text1) as TextView).text =
            getWeekDayName(position + 1)
        return view
    }

    private fun getWeekDayName(weekDayInt: Int): String {
        return when (weekDayInt) {
            1 -> context.getString(R.string.mon)
            2 -> context.getString(R.string.tue)
            3 -> context.getString(R.string.wed)
            4 -> context.getString(R.string.thu)
            5 -> context.getString(R.string.fri)
            6 -> context.getString(R.string.sat)
            7 -> context.getString(R.string.sun)
            else -> ""
        }
    }
}
