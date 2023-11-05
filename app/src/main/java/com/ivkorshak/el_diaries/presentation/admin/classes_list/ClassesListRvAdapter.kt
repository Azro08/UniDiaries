package com.ivkorshak.el_diaries.presentation.admin.classes_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ivkorshak.el_diaries.data.model.ClassRoom
import com.ivkorshak.el_diaries.databinding.ClassItemBinding

class ClassesListRvAdapter(
    private val classesList: List<ClassRoom>,
    private val listener: (classRoom: ClassRoom) -> Unit
) : RecyclerView.Adapter<ClassesListRvAdapter.ClassRoomViewHolder>() {

    class ClassRoomViewHolder(
        listener: (classRoom: ClassRoom) -> Unit,
        private var binding: ClassItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private var classRoom: ClassRoom? = null
        fun bind(curClassRoom: ClassRoom) {
            binding.textViewClassName.text = curClassRoom.className
            binding.textViewRoomNum.text = curClassRoom.roomNum.toString()
            classRoom = curClassRoom
        }

        init {
            binding.imageButtonDeleteClass.setOnClickListener { listener(classRoom!!) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassRoomViewHolder {
        return ClassRoomViewHolder(
            listener,
            ClassItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return classesList.size
    }

    override fun onBindViewHolder(holder: ClassRoomViewHolder, position: Int) {
        holder.bind(classesList[position])
    }

}