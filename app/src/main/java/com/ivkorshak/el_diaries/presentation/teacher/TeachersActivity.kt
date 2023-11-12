package com.ivkorshak.el_diaries.presentation.teacher

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ivkorshak.el_diaries.R
import com.ivkorshak.el_diaries.databinding.ActivityTeachersBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeachersActivity : AppCompatActivity() {
    private var _binding :  ActivityTeachersBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTeachersBinding.inflate(layoutInflater)
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
        val navController = findNavController(R.id.teachersNavHost)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.teacherClassesFragment,
                R.id.sendFeedBackFragment,
                R.id.teacherProfileFragment
            )
        )

        val topLevelDestinations = setOf(
            R.id.teacherClassesFragment,
            R.id.sendFeedBackFragment,
            R.id.teacherProfileFragment
        )
        // Show the bottom navigation view for top-level destinations only
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in topLevelDestinations) {
                binding.teachersNavView.visibility = View.VISIBLE
            } else {
                binding.teachersNavView.visibility = View.GONE
            }
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.teachersNavView.setupWithNavController(navController)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}