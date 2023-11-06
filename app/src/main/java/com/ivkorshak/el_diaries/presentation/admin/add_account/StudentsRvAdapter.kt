package com.ivkorshak.el_diaries.presentation.admin.add_account

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ivkorshak.el_diaries.R
import com.ivkorshak.el_diaries.data.model.Students
import com.ivkorshak.el_diaries.databinding.StudentItemBinding

class StudentsRvAdapter(
    private val studentList: ArrayList<Students>,
    private val addListener: (user: Students) -> Unit,
    private val removeListener: (user: Students) -> Unit
) : RecyclerView.Adapter<StudentsRvAdapter.StudentsViewHolder>() {

    class StudentsViewHolder(
        addListener: (account: Students) -> Unit,
        removeListener: (user: Students) -> Unit,
        private var binding: StudentItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private var student: Students? = null
        fun bind(curStudent: Students) {
            binding.textViewStudentName.text = curStudent.fullName
            Glide.with(binding.root).load(curStudent.imageUrl)
                .error(R.drawable.account_circle_icon)
                .into(binding.profileImage)
            student = curStudent
        }

        init {
            binding.imageButtonAddStud.setOnClickListener {
                binding.imageButtonAddStud.visibility = View.GONE
                binding.imageButtonRemoveStud.visibility = View.VISIBLE
                addListener(student!!)
            }
            binding.imageButtonRemoveStud.setOnClickListener {
                binding.imageButtonAddStud.visibility = View.VISIBLE
                binding.imageButtonRemoveStud.visibility = View.GONE
                removeListener(student!!)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentsViewHolder {
        return StudentsViewHolder(
            addListener, removeListener, StudentItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return studentList.size
    }

    override fun onBindViewHolder(holder: StudentsViewHolder, position: Int) {
        return holder.bind(studentList[position])
    }


}