package com.app.learnbridge.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.learnbridge.R
import com.app.learnbridge.databinding.FragmentRegisterBinding

import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.app.learnbridge.LearnBridgeApplication
import com.app.learnbridge.db.User
import com.app.learnbridge.util.SessionManager
import com.app.learnbridge.viewmodel.UserViewModel

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels {
        UserViewModel.UserViewModelFactory((requireActivity().application as LearnBridgeApplication).userRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager(requireContext())

        binding.btnRegister.setOnClickListener {
            val firstName = binding.etFirstName.text.toString().trim()
            val lastName = binding.etLastName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(requireContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newUser = User(
                name = "$firstName $lastName",
                email = email,
                password = password,
                xp = 50 // Reward for registration
            )
            userViewModel.register(newUser)
        }

        userViewModel.registrationStatus.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                sessionManager.saveUserId(user.id)
                Toast.makeText(requireContext(), "Registration successful!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_authFragment_to_xpRewardFragment)
            } else {
                // Only show error if registration actually failed (not just initial null state)
                // We should probably use a better state management (like Resource or Event)
                // but for now let's just check if fields were attempted
            }
        }

        binding.tvGoToLogin.setOnClickListener {
            (parentFragment as? AuthFragment)?.switchToLogin()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
