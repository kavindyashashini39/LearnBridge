package com.app.learnbridge.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.app.learnbridge.R
import com.app.learnbridge.LearnBridgeApplication
import com.app.learnbridge.databinding.FragmentEditProfileBinding
import com.app.learnbridge.db.User
import com.app.learnbridge.util.SessionManager
import com.app.learnbridge.util.ProfilePhotoManager
import com.app.learnbridge.util.ImageLoader
import com.app.learnbridge.viewmodel.UserViewModel
import com.bumptech.glide.Glide

class EditProfileFragment : Fragment() {
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels {
        UserViewModel.UserViewModelFactory((requireActivity().application as LearnBridgeApplication).userRepository)
    }

    private var currentUser: User? = null
    private var selectedPhotoPath: String? = null

    // Image picker contract
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val userId = SessionManager(requireContext()).getUserId()
            val savedPath = ProfilePhotoManager.saveProfilePhoto(requireContext(), userId, it)
            if (savedPath != null) {
                selectedPhotoPath = savedPath

                // Clear Glide cache and reload image
                Glide.with(this).clear(binding.ivEditProfileImage)
                binding.ivEditProfileImage.setImageBitmap(null)

                // Load the new image with skipCache to ensure it refreshes
                ImageLoader.loadCourseImageSkipCache(binding.ivEditProfileImage, savedPath, isCircle = true)
                Toast.makeText(requireContext(), R.string.photo_selected, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), R.string.photo_save_failed, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val userId = SessionManager(requireContext()).getUserId()

        userViewModel.getUserById(userId).observe(viewLifecycleOwner) { user ->
            user?.let {
                currentUser = it
                binding.etEditName.setText(it.name)
                binding.etEditEmail.setText(it.email)

                // Load existing profile photo if available
                if (!it.profilePhotoPath.isNullOrEmpty()) {
                    selectedPhotoPath = it.profilePhotoPath
                    ImageLoader.loadCourseImage(binding.ivEditProfileImage, it.profilePhotoPath, isCircle = true)
                }
            }
        }

        // FAB to change profile photo
        binding.fabChangePhoto.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.btnSaveProfile.setOnClickListener {
            val name = binding.etEditName.text.toString()
            val email = binding.etEditEmail.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty()) {
                currentUser?.let {
                    val updatedUser = it.copy(
                        name = name,
                        email = email,
                        profilePhotoPath = selectedPhotoPath  // Include profile photo path
                    )
                    userViewModel.updateUser(updatedUser)
                    Toast.makeText(requireContext(), R.string.profile_updated, Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            } else {
                Toast.makeText(requireContext(), R.string.error_fill_fields, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
