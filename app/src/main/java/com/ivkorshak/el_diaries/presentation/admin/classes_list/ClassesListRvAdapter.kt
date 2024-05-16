package com.ivkorshak.el_diaries.presentation.admin.classes_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ivkorshak.el_diaries.R
import com.ivkorshak.el_diaries.data.model.ClassRoom
import com.ivkorshak.el_diaries.databinding.ClassItemBinding

class ClassesListRvAdapter(
    private val classesList: List<ClassRoom>,
    private val userRole: String,
    private val deleteListener: (classRoom: ClassRoom) -> Unit,
    private val detailsListener: (classRoom: ClassRoom) -> Unit
) : RecyclerView.Adapter<ClassesListRvAdapter.ClassRoomViewHolder>() {

    class ClassRoomViewHolder(
        private val userRole: String,
        listener: (classRoom: ClassRoom) -> Unit,
        detailsListener: (classRoom: ClassRoom) -> Unit,
        private var binding: ClassItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private var classRoom: ClassRoom? = null
        fun bind(curClassRoom: ClassRoom) = with(binding) {
            val context = itemView.context
            if (userRole != "admin") {
                imageButtonDeleteClass.visibility = View.GONE
            }
            textViewClassStartTime.text = curClassRoom.startTime
            textViewClassEndTime.text = curClassRoom.endTime
            textViewClassName.text = curClassRoom.className
            textViewRoomNum.text = curClassRoom.roomNum.toString()

            when (curClassRoom.classType) {
                context.getString(R.string.lecture) -> imageViewClassType.setBackgroundResource(R.drawable.lecture_shape)
                context.getString(R.string.practice) -> imageViewClassType.setBackgroundResource(
                    R.drawable.pz_shape
                )

                context.getString(R.string.laboratory) -> imageViewClassType.setBackgroundResource(R.drawable.lab_shape)
            }

            classRoom = curClassRoom
        }

        init {
            binding.imageButtonDeleteClass.setOnClickListener { listener(classRoom!!) }
            binding.layoutContainer.setOnClickListener { detailsListener(classRoom!!) }
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