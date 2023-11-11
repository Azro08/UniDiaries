package com.ivkorshak.el_diaries.presentation.admin.classes_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ivkorshak.el_diaries.data.model.ClassRoom
import com.ivkorshak.el_diaries.databinding.ClassItemBinding
import com.ivkorshak.el_diaries.util.AuthManager
import javax.inject.Inject

class ClassesListRvAdapter(
    private val classesList: List<ClassRoom>,
    private val userRole: String,
    private val deleteListener: (classRoom: ClassRoom) -> Unit,
    private val detailsListener : (classRoom : ClassRoom) -> Unit
) : RecyclerView.Adapter<ClassesListRvAdapter.ClassRoomViewHolder>() {

    class ClassRoomViewHolder(
        private val userRole: String,
        listener: (classRoom: ClassRoom) -> Unit,
        detailsListener : (classRoom : ClassRoom) -> Unit,
        private var binding: ClassItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private var classRoom: ClassRoom? = null
        fun bind(curClassRoom: ClassRoom) {
            if (userRole != "admin") {
                binding.imageButtonDeleteClass.visibility = View.GONE
            }
            binding.textViewClassName.text = curClassRoom.className
            binding.textViewRoomNum.text = curClassRoom.roomNum.toString()
            classRoom = curClassRoom
        }

        init {
            binding.imageButtonDeleteClass.setOnClickListener { listener(classRoom!!) }
            binding.layoutContainer.setOnClickListener{detailsListener(classRoom!!)}
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassRoomViewHolder {
        return ClassRoomViewHolder(
            userRole,
            deleteListener,
            detailsListener,
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