package com.app.learnbridge.ui.fragments

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
import com.app.learnbridge.databinding.FragmentCourseDetailsBinding
import com.app.learnbridge.util.SessionManager
import com.app.learnbridge.util.ImageLoader
import com.app.learnbridge.viewmodel.CourseViewModel

class CourseDetailsFragment : Fragment() {
    private var _binding: FragmentCourseDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CourseViewModel by viewModels {
        CourseViewModel.CourseViewModelFactory((requireActivity().application as LearnBridgeApplication).courseRepository)
    }

    private val subscriptionViewModel: com.app.learnbridge.viewmodel.SubscriptionViewModel by viewModels {
        com.app.learnbridge.viewmodel.SubscriptionViewModel.SubscriptionViewModelFactory((requireActivity().application as LearnBridgeApplication).subscriptionRepository)
    }

    private val userViewModel: com.app.learnbridge.viewmodel.UserViewModel by viewModels {
        com.app.learnbridge.viewmodel.UserViewModel.UserViewModelFactory((requireActivity().application as LearnBridgeApplication).userRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCourseDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val courseId = arguments?.getInt("courseId") ?: -1
        val userId = SessionManager(requireContext()).getUserId()

        if (courseId != -1) {
            viewModel.getCourseById(courseId).observe(viewLifecycleOwner) { course ->
                course?.let {
                    binding.tvCourseTitle.text = it.title
                    binding.tvDescription.text = it.description
                    binding.tvInstructor.text = "by LearnBridge Expert" // Placeholder
                    binding.tvDuration.text = "${it.durationHours} Hours"
                    binding.tvRating.text = String.format("%.1f ★ (%d)", it.averageRating, it.ratingCount)
                    binding.tvCurriculumContent.text = if (it.curriculum.isBlank()) {
                        "Curriculum not added yet."
                    } else {
                        it.curriculum.lines()
                            .filter { line -> line.isNotBlank() }
                            .mapIndexed { index, line -> "${index + 1}. $line" }
                            .joinToString("\n")
                    }
                    
                    // Load course image with proper fallback logic
                    ImageLoader.loadCourseImage(binding.ivCourseBanner, it.imageUrl)
                }
            }

            viewModel.isEnrolled(userId, courseId).observe(viewLifecycleOwner) { enrolled ->
                if (enrolled) {
                    binding.btnEnroll.text = "Continue Learning"
                    binding.btnEnroll.setOnClickListener {
                        val bundle = Bundle().apply {
                            putInt("courseId", courseId)
                        }
                        findNavController().navigate(R.id.action_courseDetailsFragment_to_coursePlayerFragment, bundle)
                    }
                } else {
                    binding.btnEnroll.text = "Enroll Now"
                    binding.btnEnroll.setOnClickListener {
                        viewModel.getCourseById(courseId).observe(viewLifecycleOwner) { course ->
                            if (course?.isPremium == true) {
                                subscriptionViewModel.getLatestSubscription(userId).observe(viewLifecycleOwner) { subscription ->
                                    if (subscription?.plan == "Premium") {
                                        performEnrollment(userId, courseId)
                                    } else {
                                        Toast.makeText(requireContext(), "Upgrade to Premium to access this course", Toast.LENGTH_LONG).show()
                                        findNavController().navigate(R.id.subscriptionFragment)
                                    }
                                }
                            } else {
                                performEnrollment(userId, courseId)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun performEnrollment(userId: Int, courseId: Int) {
        viewModel.enroll(userId, courseId)
        userViewModel.addXp(userId, 50)
        Toast.makeText(requireContext(), "Successfully enrolled! +50 XP", Toast.LENGTH_SHORT).show()
        binding.btnEnroll.text = "Continue Learning"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
