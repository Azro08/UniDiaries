package com.ivkorshak.el_diaries.presentation.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.ivkorshak.el_diaries.R
import com.ivkorshak.el_diaries.data.model.Users
import com.ivkorshak.el_diaries.databinding.FragmentProfileBinding
import com.ivkorshak.el_diaries.presentation.auth.AuthActivity
import com.ivkorshak.el_diaries.util.AuthManager
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    private var userId = ""

    @Inject
    lateinit var authManager: AuthManager

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private var imageUri = Uri.parse("")
    private var imageUrl = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setMediaPicker()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setMenu()
        if (firebaseAuth.currentUser?.uid.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "You are not authorized", Toast.LENGTH_SHORT).show()
            logout()
        } else {
            userId = firebaseAuth.currentUser?.uid!!
        }
        binding.profileImage.setOnClickListener {
            setProfileImage()
        }
        viewModelOutputs()
    }

    private fun setProfileImage() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun setMediaPicker() {
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                Glide.with(binding.root).load(uri)
                    .error(R.drawable.account_circle_icon)
                    .into(binding.profileImage)
                imageUri = uri
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context?.contentResolver?.takePersistableUriPermission(uri, flag)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    }

    private fun viewModelOutputs() {
        lifecycleScope.launch {
            viewModel.getUser(userId)
            viewModel.users.collect { state ->
                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Success -> {
                        if (state.data != null) displayProfileDetails(state.data)
                        else Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT)
                            .show()
                    }

                    is ScreenState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun displayProfileDetails(user: Users) {
        binding.editTextAddress.setText(user.address)
        binding.editTextPhoneNum.setText(user.phoneNumber)
        Glide.with(binding.root).load(user.imageUrl)
            .error(R.drawable.account_circle_icon)
            .into(binding.profileImage)
    }

    private fun setMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.profile_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.itemEditProfile -> {
                        editProfile()
                    }

                    R.id.itemLogout -> {
                        logout()
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun areAllFieldsFilled(): Boolean {
        val address = binding.editTextAddress.text.toString()
        val phoneNumber = binding.editTextPhoneNum.text.toString()

        return !(address.isEmpty() || phoneNumber.isEmpty())

    }

    private fun editProfile() {
        binding.buttonSaveProfile.visibility = View.VISIBLE
        binding.editTextAddress.isFocusable = true
        binding.editTextPhoneNum.isFocusable = true
        binding.editTextPassword.visibility = View.VISIBLE
        binding.editTextOldPassword.visibility = View.VISIBLE
        binding.profileImage.isClickable = true

        binding.buttonSaveProfile.setOnClickListener {
            if (areAllFieldsFilled()) {
                if (imageUri != Uri.parse("")) uploadImage()
                else saveUser()
            } else Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT)
                .show()
        }

    }

    private fun uploadImage() {
        lifecycleScope.launch {
            Log.d("ImageUri", imageUri.toString())
            viewModel.uploadImageAndGetUri(firebaseAuth.currentUser?.uid!!, imageUri)
            viewModel.imageUploaded.collect { state ->
                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Success -> {
                        imageUrl = state.data.toString()
                        saveUser()
                    }

                    is ScreenState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun saveUser() {
        val address = binding.editTextAddress.text.toString()
        val phoneNumber = binding.editTextPhoneNum.text.toString()
        var password = binding.editTextPassword.text.toString()
        var oldPassword = ""
        var email = ""
        if (password.isEmpty()) password = ""
        else{
            if (binding.editTextOldPassword.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Old password is required", Toast.LENGTH_SHORT)
                    .show()
            } else{
                oldPassword = binding.editTextOldPassword.text.toString()
                email = authManager.getUser()
            }
        }

        val updatedFields: Map<String, Any> = if (imageUri != Uri.parse("")) {
            mapOf(
                "address" to address,
                "phoneNumber" to phoneNumber,
                "imageUrl" to imageUri
            )
        } else {
            mapOf(
                "address" to address,
                "phoneNumber" to phoneNumber
            )
        }

        lifecycleScope.launch {
            viewModel.saveUser(firebaseAuth.currentUser?.uid!!, updatedFields, password, oldPassword, email)
            viewModel.userSaved.collect { state ->
                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Success -> {
                        Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT)
                            .show()
                    }

                    is ScreenState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun logout() {
        firebaseAuth.signOut()
        authManager.removeUser()
        authManager.removeRole()
        requireActivity().startActivity(Intent(requireActivity(), AuthActivity::class.java))
        requireActivity().finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}