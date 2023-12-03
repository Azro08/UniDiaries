package com.ivkorshak.el_diaries.presentation.common.language

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.ivkorshak.el_diaries.databinding.FragmentLanguageBinding
import com.ivkorshak.el_diaries.presentation.admin.MainActivity
import com.ivkorshak.el_diaries.util.LangUtils
import com.ivkorshak.el_diaries.util.setLocale
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LanguageFragment : DialogFragment() {
    private var _binding: FragmentLanguageBinding? = null
    private val binding get() = _binding!!
    private val langList = listOf("en", "ru")

    @Inject
    lateinit var langUtils: LangUtils
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentLanguageBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this.activity)
        builder.run { setView(binding.root) }
        setSpinner()
        binding.buttonSaveLang.setOnClickListener {
            if (binding.spinnerLanguage.selectedItem != null) {
                setLanguage()
            }
        }
        return builder.create()
    }

    private fun setLanguage() {
        val lang = binding.spinnerLanguage.selectedItem.toString()
        requireContext().setLocale(lang)
        langUtils.saveLang(lang)
        startActivity(Intent(requireActivity(), MainActivity::class.java))
        requireActivity().finish()
    }

    private fun setSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            langList
        )
        binding.spinnerLanguage.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}