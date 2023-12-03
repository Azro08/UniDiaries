package com.ivkorshak.el_diaries.presentation.common.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ivkorshak.el_diaries.R
import com.ivkorshak.el_diaries.databinding.FragmentSettingsBinding
import com.ivkorshak.el_diaries.presentation.common.language.LanguageFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private var _binding : FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewLanguage.setOnClickListener {
            val languageFragment = LanguageFragment()
            languageFragment.show(parentFragmentManager, "languageFragment")

        }
        binding.textViewFeedback.setOnClickListener {
            findNavController().navigate(R.id.nav_settings_send_feedback)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}