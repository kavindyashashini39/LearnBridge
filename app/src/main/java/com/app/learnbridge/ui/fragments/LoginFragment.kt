package com.app.learnbridge.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.learnbridge.R
import com.app.learnbridge.databinding.FragmentLoginBinding

import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.app.learnbridge.LearnBridgeApplication
import com.app.learnbridge.util.SessionManager
import com.app.learnbridge.ui.AdminActivity
import com.app.learnbridge.viewmodel.UserViewModel

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels {
        UserViewModel.UserViewModelFactory((requireActivity().application as LearnBridgeApplication).userRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager(requireContext())

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
                sessionManager.saveUserId(user.id)
                if (user.isAdmin) {
                    startActivity(Intent(requireContext(), AdminActivity::class.java))
                    requireActivity().finish()
                } else {
                    // Check if user already completed survey
                    if (user.surveyCompleted) {
                        // Survey already done, go directly to home
                        findNavController().navigate(R.id.mainFragment)
                    } else {
                        // First login, show survey flow
                        findNavController().navigate(R.id.xpRewardFragment)
                    }
                }
            }
        }

        userViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvGoToSignUp.setOnClickListener {
            (parentFragment as? AuthFragment)?.switchToSignUp()
        }

        binding.tvAdminLogin.setOnClickListener {
            findNavController().navigate(R.id.adminLoginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
