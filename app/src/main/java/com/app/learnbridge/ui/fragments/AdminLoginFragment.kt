package com.app.learnbridge.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.learnbridge.LearnBridgeApplication
import com.app.learnbridge.R
import com.app.learnbridge.databinding.FragmentAdminLoginBinding
import com.app.learnbridge.ui.AdminActivity
import com.app.learnbridge.util.SessionManager
import com.app.learnbridge.viewmodel.UserViewModel

class AdminLoginFragment : Fragment() {
    private var _binding: FragmentAdminLoginBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels {
        UserViewModel.UserViewModelFactory((requireActivity().application as LearnBridgeApplication).userRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager(requireContext())

        // Pre-fill admin credentials
        binding.etEmail.setText("admin@learnbridge.com")
        binding.etPassword.setText("admin")

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            userViewModel.login(email, password)
        }

        userViewModel.loginStatus.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                if (user.isAdmin) {
                    sessionManager.saveUserId(user.id)
                    Toast.makeText(requireContext(), "Admin login successful!", Toast.LENGTH_SHORT).show()
                    // Launch AdminActivity instead of navigating
                    val intent = Intent(requireContext(), AdminActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    Toast.makeText(requireContext(), "This account is not an admin account", Toast.LENGTH_SHORT).show()
                }
            }
        }

        userViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvBackToLogin.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

