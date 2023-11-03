package com.ivkorshak.el_diaries.presentation.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ivkorshak.el_diaries.R
import com.ivkorshak.el_diaries.databinding.ActivityMainBinding
import com.ivkorshak.el_diaries.presentation.auth.AuthActivity
import com.ivkorshak.el_diaries.presentation.student.StudentsActivity
import com.ivkorshak.el_diaries.presentation.teacher.TeachersActivity
import com.ivkorshak.el_diaries.util.AuthManager
import com.ivkorshak.el_diaries.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var authManager: AuthManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (!authManager.isLoggedIn()) {
            startActivity(Intent(this, AuthActivity::class.java))
            this.finish()
        } else getRole()

        setBottomNavBar()

    }

    private fun setBottomNavBar() {
        val navController = findNavController(R.id.nav_host)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.accountsListFragment, R.id.classesListFragment, R.id.feedBackFragment, R.id.profileFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
    }

    private fun getRole() {
        val role = authManager.getRole()
        if (role.isEmpty()) {
            startActivity(Intent(this, AuthActivity::class.java))
        } else {
            when (role) {
                Constants.TEACHER -> {
                    startActivity(Intent(this, TeachersActivity::class.java))
                    this.finish()
                }

                Constants.STUDENT -> {
                    startActivity(Intent(this, StudentsActivity::class.java))
                    this.finish()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}