package com.ivkorshak.el_diaries.presentation.admin.accounts_list

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.ivkorshak.el_diaries.R
import com.ivkorshak.el_diaries.data.Users
import com.ivkorshak.el_diaries.databinding.FragmentAccountsListBinding
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
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

    override fun onResume() {
        super.onResume()
        refreshFragment()
        viewModelOutputs()
    }

    private fun refreshFragment() {
        binding.loadingGif.visibility = View.VISIBLE
        binding.rvAccountsList.visibility = View.GONE
        binding.textViewError.visibility = View.GONE
        usersList.clear()
        Handler(Looper.myLooper()!!).postDelayed({

            // This code will run after a 3-second delay
            viewModelOutputs()
            binding.swipeRefreshLayout.isRefreshing = false
            binding.loadingGif.visibility = View.GONE
            binding.rvAccountsList.visibility = View.VISIBLE
        }, 3000) // 3000 milliseconds (3 seconds)
    }

    private fun viewModelOutputs() {
        lifecycleScope.launch {
            viewModel.users.collect { accounts ->
                Log.d("AccountsListFragment", "accounts: ${accounts.data?.size}")
                processResponse(accounts)
            }
        }
    }

    private fun processResponse(state: ScreenState<List<Users>?>) {
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

    @SuppressLint("NotifyDataSetChanged")
    private fun displayUsers(users: List<Users>) {
        usersList.clear()
        usersList.addAll(users)
        rvAdapter = AccountsListRvAdapter(usersList) {
            showConfirmationDialog(it)
        }
        rvAdapter?.notifyDataSetChanged()
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
            viewModel.userDeleted.collect {
                deleteUserState(it, user)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun deleteUserState(state: ScreenState<String?>, user: Users) {
        when (state) {
            is ScreenState.Loading -> {}
            is ScreenState.Success -> {
                usersList.remove(user)
                rvAdapter?.notifyDataSetChanged()
                Toast.makeText(requireContext(), "User deleted", Toast.LENGTH_SHORT).show()
            }

            is ScreenState.Error -> {
                handleError(state.message!!)
            }
        }
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