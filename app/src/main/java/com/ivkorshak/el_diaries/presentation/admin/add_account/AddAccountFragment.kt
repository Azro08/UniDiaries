package com.ivkorshak.el_diaries.presentation.admin.add_account

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.ivkorshak.el_diaries.data.model.Users
import com.ivkorshak.el_diaries.databinding.FragmentAddAccountBinding
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class AddAccountFragment : Fragment() {
    private var _binding: FragmentAddAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddAccountViewModel by viewModels()

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    private val roles = listOf("teacher", "student")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddAccountBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.buttonSave.setOnClickListener {
            if (areAllFieldsFilled()) createUser()
                else Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT)
                    .show()
        }
        binding.buttonPickBirthDate.setOnClickListener {
            showDatePickerDialog()
        }
        val adapter = ArrayAdapter(
            requireContext(),
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            roles
        )
        binding.spinnerRole.adapter = adapter
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _: DatePicker, year: Int, month: Int, day: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, day)
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val formattedDate = sdf.format(selectedDate.time)
                binding.textViewBirthDate.text = formattedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    private fun createUser() = with(binding) {
        val email = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                saveUser(task.result.user?.uid)
            } else Toast.makeText(
                requireContext(),
                task.exception?.message.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun saveUser(uid: String?) = with(binding) {
        if (uid != null) {
            val email = editTextEmail.text.toString()
            val firstName = editTextFirstName.text.toString()
            val lastName = editTextLastName.text.toString()
            val address = editTextAddress.text.toString()
            val phoneNumber = editTextPhoneNumber.text.toString()
            val birthDate = textViewBirthDate.text.toString()
            val role = spinnerRole.selectedItem.toString()

            val newUser = Users(
                id = uid,
                email = email,
                firstName = firstName,
                lastName = lastName,
                address = address,
                phoneNumber = phoneNumber,
                birthDate = birthDate,
                role = role
            )
            lifecycleScope.launch {
                viewModel.saveUser(newUser)
                viewModel.userCreated.collect {
                    processUserCreatedState(it)
                }
            }
        }
    }

    private fun processUserCreatedState(state: ScreenState<String?>) {
        when (state) {
            is ScreenState.Loading -> {}
            is ScreenState.Success -> {
                Toast.makeText(requireContext(), "User created successfully", Toast.LENGTH_SHORT)
                    .show()
                findNavController().popBackStack()
            }

            is ScreenState.Error -> {
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun areAllFieldsFilled(): Boolean = with(binding) {

        val email = editTextEmail.text.toString()
        val firstName = editTextFirstName.text.toString()
        val lastName = editTextLastName.text.toString()
        val password = editTextPassword.text.toString()
        val address = editTextAddress.text.toString()
        val phoneNumber = editTextPhoneNumber.text.toString()
        val birthDate = textViewBirthDate.text.toString()
        val role = spinnerRole.selectedItem.toString()

        if (email.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || password.isEmpty() || address.isEmpty() || phoneNumber.isEmpty() || birthDate.isEmpty() || role.isEmpty()
        ) {
            return false // At least one field is empty
        }

        return true // All fields are filled
    }

}