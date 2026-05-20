package com.app.learnbridge.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.app.learnbridge.LearnBridgeApplication
import com.app.learnbridge.databinding.FragmentChangePasswordBinding
import com.app.learnbridge.db.User
import com.app.learnbridge.util.SessionManager
import com.app.learnbridge.viewmodel.UserViewModel

class ChangePasswordFragment : Fragment() {
    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels {
        UserViewModel.UserViewModelFactory((requireActivity().application as LearnBridgeApplication).userRepository)
    }

    private var currentUser: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = SessionManager(requireContext()).getUserId()

        userViewModel.getUserById(userId).observe(viewLifecycleOwner) { user ->
            currentUser = user
        }

        binding.btnChangePassword.setOnClickListener {
            val currentPwd = binding.etCurrentPassword.text.toString()
            val newPwd = binding.etNewPassword.text.toString()
            val confirmPwd = binding.etConfirmNewPassword.text.toString()

            if (currentPwd.isEmpty() || newPwd.isEmpty() || confirmPwd.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPwd != confirmPwd) {
                Toast.makeText(requireContext(), "New passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            currentUser?.let { user ->
                if (user.password == currentPwd) {
                    val updatedUser = user.copy(password = newPwd)
                    userViewModel.updateUser(updatedUser)
                    Toast.makeText(requireContext(), "Password changed successfully", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                } else {
                    Toast.makeText(requireContext(), "Incorrect current password", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
