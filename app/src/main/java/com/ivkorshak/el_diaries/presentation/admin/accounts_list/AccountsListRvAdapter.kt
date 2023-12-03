package com.ivkorshak.el_diaries.presentation.admin.accounts_list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ivkorshak.el_diaries.R
import com.ivkorshak.el_diaries.data.model.Users
import com.ivkorshak.el_diaries.databinding.AccountItemBinding

class AccountsListRvAdapter(
    private var accountsList: List<Users>,
    private val listener: (account: Users) -> Unit
) : RecyclerView.Adapter<AccountsListRvAdapter.AccountViewHolder>() {

    class AccountViewHolder(
        listener: (account: Users) -> Unit,
        private var binding: AccountItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private var account: Users? = null
        fun bind(curAccount: Users) {
            val fullNameAndRole =
                "${curAccount.firstName} ${curAccount.lastName} (${curAccount.role})"
            binding.textViewNameRole.text = fullNameAndRole
            binding.textViewEmail.text = curAccount.email
            Glide.with(binding.root).load(curAccount.imageUrl)
                .error(R.drawable.account_circle_icon)
                .into(binding.profileImage)
            account = curAccount
        }

        init {
            binding.imageButtonDeleteAccount.setOnClickListener { listener(account!!) }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateFoodList(newUsersList: List<Users>) {
        accountsList = newUsersList.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        return AccountViewHolder(
            listener,
            AccountItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return accountsList.size
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        holder.bind(accountsList[position])
    }

}