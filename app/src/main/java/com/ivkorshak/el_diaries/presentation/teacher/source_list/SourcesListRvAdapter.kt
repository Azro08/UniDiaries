package com.ivkorshak.el_diaries.presentation.teacher.source_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ivkorshak.el_diaries.databinding.SourceItemBinding

class SourcesListRvAdapter(
    private val sourceList: List<String>,
    private val deleteListener: (source: String) -> Unit,
    private val linkListener: (source: String) -> Unit
) : RecyclerView.Adapter<SourcesListRvAdapter.SourceViewHolder>() {

    class SourceViewHolder(
        deleteListener: (source: String) -> Unit,
        linkListener: (source: String) -> Unit,
        private val binding: SourceItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private var source: String? = null
        fun bind(curSource: String) {
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