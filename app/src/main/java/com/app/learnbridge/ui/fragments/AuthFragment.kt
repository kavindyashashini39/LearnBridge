package com.app.learnbridge.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.learnbridge.R
import com.app.learnbridge.databinding.FragmentAuthBinding

class AuthFragment : Fragment() {
    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        switchToSignUp()

        binding.tvTabSignUp.setOnClickListener { switchToSignUp() }
        binding.tvTabLogin.setOnClickListener { switchToLogin() }
        binding.ivGoogle.setOnClickListener {
            findNavController().navigate(R.id.action_authFragment_to_googleSignInFragment)
        }
    }

    fun switchToSignUp() {
        binding.tvTabSignUp.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
        binding.tvTabLogin.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        childFragmentManager.beginTransaction()
            .replace(R.id.authContainer, RegisterFragment())
            .commit()
    }

    fun switchToLogin() {
        binding.tvTabLogin.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
        binding.tvTabSignUp.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        childFragmentManager.beginTransaction()
            .replace(R.id.authContainer, LoginFragment())
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
