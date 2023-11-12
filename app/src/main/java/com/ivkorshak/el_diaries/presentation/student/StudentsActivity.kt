package com.ivkorshak.el_diaries.presentation.student

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ivkorshak.el_diaries.R
import com.ivkorshak.el_diaries.databinding.ActivityStudentsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudentsActivity : AppCompatActivity() {
    private var _binding : ActivityStudentsBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityStudentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(
                resources.getColor(
                    R.color.blue,
                    theme
                )
            )
        )
        setBottomNavBar()
    }

    private fun setBottomNavBar() {
        val navController = findNavController(R.id.studentsNavHost)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.classesFragment,
                R.id.sendFeedBackFragment,
                R.id.commonProfileFragment
            )
        )

        val topLevelDestinations = setOf(
            R.id.classesFragment,
            R.id.sendFeedBackFragment,
            R.id.commonProfileFragment
        )
        // Show the bottom navigation view for top-level destinations only
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in topLevelDestinations) {
                binding.studentsNavView.visibility = View.VISIBLE
            } else {
                binding.studentsNavView.visibility = View.GONE
            }
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.studentsNavView.setupWithNavController(navController)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}