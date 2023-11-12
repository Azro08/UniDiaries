package com.ivkorshak.el_diaries.presentation.common.source_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ivkorshak.el_diaries.databinding.SourceItemBinding

class SourcesListRvAdapter(
    private val role: String,
    private val sourceList: List<String>,
    private val deleteListener: (source: String) -> Unit,
    private val linkListener: (source: String) -> Unit
) : RecyclerView.Adapter<SourcesListRvAdapter.SourceViewHolder>() {

    class SourceViewHolder(
        private val role: String,
        deleteListener: (source: String) -> Unit,
        linkListener: (source: String) -> Unit,
        private val binding: SourceItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private var source: String? = null
        fun bind(curSource: String) {
            if (role == "student") binding.buttonDeleteSource.visibility = View.GONE
            binding.textViewSource.text = curSource
            source = curSource
        }

        init {
            binding.buttonDeleteSource.setOnClickListener { deleteListener(source!!) }
            binding.textViewSource.setOnClickListener { linkListener(source!!) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourceViewHolder {
        return SourceViewHolder(
            role,
            deleteListener,
            linkListener,
            SourceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return sourceList.size
    }

    override fun onBindViewHolder(holder: SourceViewHolder, position: Int) {
        holder.bind(sourceList[position])
    }

}