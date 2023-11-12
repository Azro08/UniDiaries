package com.ivkorshak.el_diaries.presentation.common.homework

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ivkorshak.el_diaries.databinding.HomeworkItemBinding

class HomeWorksRvAdapter(
    private val role : String,
    private val homeworks: List<String>,
    private val listener: (homeWork: String) -> Unit
) : RecyclerView.Adapter<HomeWorksRvAdapter.HomeWorkViewHolder>() {

    class HomeWorkViewHolder(
        private val role: String,
        listener: (homeWork: String) -> Unit,
        private val binding: HomeworkItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private var homeWork: String? = null

        fun bind(curHomeWork: String) {
            if (role == "student") binding.buttonDeleteHomeWork.visibility = View.GONE
            binding.textViewHomeWork.text = curHomeWork
            homeWork = curHomeWork
        }

        init {
            binding.buttonDeleteHomeWork.setOnClickListener { listener(homeWork!!) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeWorkViewHolder {
        return HomeWorkViewHolder(role, listener, HomeworkItemBinding.inflate(LayoutInflater.from(parent.context),  parent, false))
    }

    override fun getItemCount(): Int {
        return homeworks.size
    }

    override fun onBindViewHolder(holder: HomeWorkViewHolder, position: Int) {
        holder.bind(homeworks[position])
    }

}