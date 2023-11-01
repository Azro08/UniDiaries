package com.ivkorshak.el_diaries.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.ivkorshak.el_diaries.databinding.FragmentLoginBinding
import com.ivkorshak.el_diaries.presentation.MainActivity
import com.ivkorshak.el_diaries.util.AuthManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var authManager: AuthManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.buttonLogin.setOnClickListener {
            login()
        }
    }

    private fun login() = with(binding) {
        val email = editTextLoginEmail.text.toString()
        val password = editTextLoginPassword.text.toString()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    authManager.saveUer(email)
                    Toast.makeText(
                        requireContext(),
                        "Logged in",
                        Toast.LENGTH_LONG
                    ).show()
                    requireActivity().startActivity(
                        Intent(
                            requireContext(),
                            MainActivity::class.java
                        )
                    )
                    requireActivity().finish()
                } else {
                    Toast.makeText(
                        requireContext(),
                        task.exception?.message.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }

            }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}