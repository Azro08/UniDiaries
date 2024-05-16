package com.ivkorshak.el_diaries.presentation.auth.reset_password

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.ivkorshak.el_diaries.R
import com.ivkorshak.el_diaries.databinding.FragmentResetPasswordBinding
import javax.inject.Inject

class ResetPasswordFragment : DialogFragment() {
    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentResetPasswordBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext())
        dialog.setContentView(binding.root)

        binding.buttonResetPassword.setOnClickListener {
            val email = binding.editTextResetEmail.text.toString()
            if (email.isNotEmpty()) resetPassword(email)
        }

        return dialog
    }

    private fun resetPassword(email: String) {
        Log.d("ResetPassword", "resetPassword: $email")
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.reset_link_was_sent_to_your_email), Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    task.exception?.localizedMessage ?: getString(R.string.error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
