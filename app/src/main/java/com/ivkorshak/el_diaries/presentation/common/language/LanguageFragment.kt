package com.ivkorshak.el_diaries.presentation.common.language

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.ivkorshak.el_diaries.R
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

    @Inject
    lateinit var langUtils: LangUtils
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentLanguageBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this.activity)
        builder.run { setView(binding.root) }
        bindViewModelInputs()
        checkLanguage()
        return builder.create()
    }

    private fun checkLanguage() = with(binding) {
        val lang = resources.configuration.locales[0]

        if (lang.language == LANG_EN) {
            checkedEn.visibility = View.VISIBLE
            lanEn.setBackgroundResource(R.drawable.bg_lan_selected)
        } else {
            checkedRus.visibility = View.VISIBLE
            lanRus.setBackgroundResource(R.drawable.bg_lan_selected)
        }
    }

    private fun bindViewModelInputs() = with(binding) {

        lanEn.setOnClickListener {
            checkedEn.visibility = View.VISIBLE
            checkedRus.visibility = View.GONE
            lanEn.setBackgroundResource(R.drawable.bg_lan_selected)
            lanRus.setBackgroundResource(R.drawable.bg_view_date)
            setLanguage(LANG_EN)
        }

        lanRus.setOnClickListener {
            checkedRus.visibility = View.VISIBLE
            checkedEn.visibility = View.GONE
            lanRus.setBackgroundResource(R.drawable.bg_lan_selected)
            lanEn.setBackgroundResource(R.drawable.bg_view_date)
            setLanguage(LANG_RUS)
        }
    }


    private fun setLanguage(language: String) {
        requireContext().setLocale(language)
        langUtils.saveLang(language)
        startActivity(Intent(requireActivity(), MainActivity::class.java))
        requireActivity().finish()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val LANG_EN = "en"
        private const val LANG_RUS = "ru"
    }
}