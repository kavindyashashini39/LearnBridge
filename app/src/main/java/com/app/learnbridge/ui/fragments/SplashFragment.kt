package com.app.learnbridge.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.learnbridge.R

import androidx.lifecycle.lifecycleScope
import com.app.learnbridge.LearnBridgeApplication
import com.app.learnbridge.util.DataGenerator
import com.app.learnbridge.util.SessionManager
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val app = (requireActivity().application as LearnBridgeApplication)
        val repository = app.courseRepository
        val userRepository = app.userRepository
        val sessionManager = SessionManager(requireContext())

        lifecycleScope.launch {
            repository.seedIfEmpty(DataGenerator.getSampleCourses())
            
            Handler(Looper.getMainLooper()).postDelayed({
                if (sessionManager.isLoggedIn()) {
                    lifecycleScope.launch {
                        val user = userRepository.getUserByIdOnce(sessionManager.getUserId())
                        if (user?.goal != null) {
                            findNavController().navigate(R.id.action_splashFragment_to_mainFragment)
                        } else {
                            findNavController().navigate(R.id.action_splashFragment_to_onboardingFragment)
                        }
                    }
                } else {
                    findNavController().navigate(R.id.action_splashFragment_to_onboardingFragment)
                }
            }, 2000)
        }
    }
}
