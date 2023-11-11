package com.ivkorshak.el_diaries.presentation.teacher.students_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ivkorshak.el_diaries.R
import com.ivkorshak.el_diaries.data.model.Students
import com.ivkorshak.el_diaries.databinding.ClassStudentItemBinding

class StudentsRvAdapter(
    private val students: List<Students>,
    private val listener: (account: Students) -> Unit
) : RecyclerView.Adapter<StudentsRvAdapter.StudentViewHolder>() {

    class StudentViewHolder(
        listener: (account: Students) -> Unit,
        private var binding: ClassStudentItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private var account: Students? = null
        fun bind(curAccount: Students) {
            binding.textViewStudentName.text = curAccount.fullName
            Glide.with(binding.root).load(curAccount.imageUrl)
                .error(R.drawable.account_circle_icon)
                .into(binding.profileImage)
            account = curAccount
        }

        init {
            binding.root.setOnClickListener { listener(account!!) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        return StudentViewHolder(
            listener,
            ClassStudentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return students.size
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(students[position])
    }

}