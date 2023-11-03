package com.ivkorshak.el_diaries.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.ivkorshak.el_diaries.databinding.ActivityAuthBinding
import com.ivkorshak.el_diaries.presentation.admin.MainActivity
import com.ivkorshak.el_diaries.presentation.student.StudentsActivity
import com.ivkorshak.el_diaries.presentation.teacher.TeachersActivity
import com.ivkorshak.el_diaries.util.AuthManager
import com.ivkorshak.el_diaries.util.Constants
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    private var _binding: ActivityAuthBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var authManager: AuthManager
    private val viewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonLogin.setOnClickListener {
            login()
        }

    }

    private fun login() = with(binding) {
        val email = editTextLoginEmail.text.toString()
        val password = editTextLoginPassword.text.toString()

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                authManager.saveUer(email)
                getUserRole()
            } else {
                Toast.makeText(
                    this@AuthActivity, task.exception?.message.toString(), Toast.LENGTH_LONG
                ).show()
            }

        }

    }

    private fun getUserRole() {
        lifecycleScope.launch {
            viewModel.userRole.collect {
                when (it) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Success -> {
                        if (it.data != null) {
                            navToUserActivity(it.data)
                        } else Toast.makeText(this@AuthActivity, "Can't get user role", Toast.LENGTH_LONG).show()
                    }
                    is ScreenState.Error -> {
                        Toast.makeText(this@AuthActivity, it.message, Toast.LENGTH_LONG).show()
                        Log.d("AuthActivity", "Error: ${it.message}")
                    }
                }
            }
        }
    }

    private fun navToUserActivity(role: String) {
        Log.d("AuthActivity", "role: $role")
        when (role) {
            Constants.ADMIN -> {
                authManager.saveRole("admin")
                startActivity(Intent(this, MainActivity::class.java))
                this.finish()
            }

            Constants.TEACHER -> {
                authManager.saveRole("teacher")
                startActivity(Intent(this, TeachersActivity::class.java))
                this.finish()
            }

            Constants.STUDENT -> {
                authManager.saveRole("student")
                startActivity(Intent(this, StudentsActivity::class.java))
                this.finish()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}