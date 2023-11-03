package com.ivkorshak.el_diaries.presentation.admin.accounts_list

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ivkorshak.el_diaries.R
import com.ivkorshak.el_diaries.data.Users
import com.ivkorshak.el_diaries.databinding.FragmentAccountsListBinding
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountsListFragment : Fragment() {
    private var _binding: FragmentAccountsListBinding? = null
    private val binding get() = _binding!!
    private var rvAdapter: AccountsListRvAdapter? = null
    private val viewModel: AccountsListViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountsListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setMenu()
        viewModelOutputs()
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshFragment()
        }
    }

    private fun viewModelOutputs() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.users.collect { accounts ->
                    processResponse(accounts)
                }
            }
        }
    }

    private fun processResponse(state: ScreenState<List<Users>?>) {
        when (state) {
            is ScreenState.Loading -> {}

            is ScreenState.Success -> {
                binding.loadingGif.visibility  = View.GONE
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

    private fun displayUsers(users: List<Users>) {
        rvAdapter = AccountsListRvAdapter(users) {
            showConfirmationDialog(it.id)
        }
        binding.rvAccountsList.setHasFixedSize(true)
        binding.rvAccountsList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAccountsList.adapter = rvAdapter
    }

    private fun showConfirmationDialog(uid: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete User?")
        builder.setPositiveButton("Yes") { _: DialogInterface?, _: Int ->
            viewModel.deleteUser(uid)
            refreshFragment()
        }
        builder.setNegativeButton(
            "No"
        ) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        builder.show()
    }

    private fun refreshFragment() {
        binding.loadingGif.visibility = View.VISIBLE
        binding.rvAccountsList.visibility = View.GONE
        binding.textViewError.visibility = View.GONE
        Handler(Looper.myLooper()!!).postDelayed({
            // This code will run after a 3-second delay
            viewModelOutputs()
            binding.swipeRefreshLayout.isRefreshing = false
            binding.loadingGif.visibility = View.GONE
            binding.rvAccountsList.visibility = View.VISIBLE
        }, 3000) // 3000 milliseconds (3 seconds)
    }

    private fun handleError(errorMsg: String) {
        binding.rvAccountsList.visibility = View.GONE
        binding.loadingGif.visibility = View.GONE
        binding.textViewError.text = errorMsg
    }

    private fun setMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.add_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.itemAdd -> {
                        findNavController().navigate(R.id.nav_acounts_to_add_acounts)
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        rvAdapter = null
    }

}