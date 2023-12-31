package com.ivkorshak.el_diaries.presentation.admin.accounts_list

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.ivkorshak.el_diaries.R
import com.ivkorshak.el_diaries.data.model.Users
import com.ivkorshak.el_diaries.databinding.FragmentAccountsListBinding
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class AccountsListFragment : Fragment() {
    private var _binding: FragmentAccountsListBinding? = null
    private val binding get() = _binding!!
    private var rvAdapter: AccountsListRvAdapter? = null
    private val viewModel: AccountsListViewModel by viewModels()
    private var usersList = arrayListOf<Users>()

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountsListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.textViewAddUser.setOnClickListener {
            findNavController().navigate(R.id.nav_accounts_to_add_accounts)
        }
        viewModelOutputs()
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
            binding.swipeRefreshLayout.isRefreshing = false
        }
        search()
    }

    private fun search() {
        binding.editTextSearchStudent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val searchText = s.toString().trim()
                performSearch(searchText)
            }
        })
    }

    private fun performSearch(query: String) {
        val filteredList = viewModel.filterFoodList(query)
        rvAdapter?.updateFoodList(filteredList)
    }

    override fun onResume() {
        super.onResume()
        Log.d("onResumed", "onResume")
        viewModel.refresh()
    }

    private fun viewModelOutputs() {
        lifecycleScope.launch {
            viewModel.users.collect { state ->
                Log.d("AccountsListFragment", "accounts: ${state.data?.size}")
                when (state) {
                    is ScreenState.Loading -> {}

                    is ScreenState.Success -> {
                        binding.loadingGif.visibility = View.GONE
                        binding.textViewError.visibility = View.GONE
                        binding.rvAccountsList.visibility = View.VISIBLE
                        if (!state.data.isNullOrEmpty()) {
                            displayUsers(state.data)
                        } else handleError("No Users found")
                    }

                    is ScreenState.Error -> {
                        handleError(state.message!!)
                    }
                }
            }
        }
    }

    private fun displayUsers(users: List<Users>) {
        Log.d("display entered", users.size.toString())
        usersList.clear()
        usersList.addAll(users)
        rvAdapter = AccountsListRvAdapter(usersList) {
            showConfirmationDialog(it)
        }
        binding.rvAccountsList.setHasFixedSize(true)
        binding.rvAccountsList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAccountsList.adapter = rvAdapter
    }

    private fun showConfirmationDialog(user: Users) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete User?")
        builder.setPositiveButton("Yes") { _: DialogInterface?, _: Int ->
            deleteUser(user)
        }
        builder.setNegativeButton(
            "No"
        ) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        builder.show()
    }

    private fun deleteUser(user: Users) {
        lifecycleScope.launch {
            viewModel.deleteUser(user.id)
            viewModel.userDeleted.collect { state ->
                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Success -> {
                        usersList.remove(user)
                        Toast.makeText(requireContext(), "User deleted", Toast.LENGTH_SHORT).show()
                    }

                    is ScreenState.Error -> {
                        handleError(state.message!!)
                    }
                }
            }
        }
    }

    private fun handleError(errorMsg: String) {
        binding.rvAccountsList.visibility = View.GONE
        binding.loadingGif.visibility = View.GONE
        binding.textViewError.visibility = View.VISIBLE
        binding.textViewError.text = errorMsg
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        rvAdapter = null
    }

}