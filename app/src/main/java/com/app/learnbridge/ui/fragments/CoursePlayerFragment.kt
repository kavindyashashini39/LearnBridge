package com.app.learnbridge.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.Toast
import android.widget.MediaController
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.learnbridge.LearnBridgeApplication
import com.app.learnbridge.R
import com.app.learnbridge.databinding.FragmentCoursePlayerBinding
import com.app.learnbridge.db.Enrollment
import com.app.learnbridge.util.SessionManager
import com.app.learnbridge.viewmodel.CourseViewModel
import com.app.learnbridge.viewmodel.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class CoursePlayerFragment : Fragment() {
    private var _binding: FragmentCoursePlayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CourseViewModel by viewModels {
        CourseViewModel.CourseViewModelFactory((requireActivity().application as LearnBridgeApplication).courseRepository)
    }

    private val userViewModel: UserViewModel by viewModels {
        UserViewModel.UserViewModelFactory((requireActivity().application as LearnBridgeApplication).userRepository)
    }

    private var currentEnrollment: Enrollment? = null
    private var ratingBottomSheet: BottomSheetDialog? = null
    private var ratingPromptShown = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCoursePlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val courseId = arguments?.getInt("courseId") ?: -1
        val userId = SessionManager(requireContext()).getUserId()

        // Setup video player
        setupVideoPlayer()

        if (courseId != -1) {
            viewModel.getCourseById(courseId).observe(viewLifecycleOwner) { course ->
                binding.tvPlayerCourseTitle.text = course?.title
            }

            viewModel.getEnrollment(userId, courseId).observe(viewLifecycleOwner) { enrollment ->
                currentEnrollment = enrollment
                updateProgressUI(enrollment?.progress ?: 0)

                if (enrollment != null && enrollment.progress >= 100 && !enrollment.hasRated && !ratingPromptShown) {
                    ratingPromptShown = true
                    showRatingBottomSheet(userId, courseId)
                }
            }
        }

        binding.btnMarkComplete.setOnClickListener {
            currentEnrollment?.let { enrollment ->
                val newProgress = (enrollment.progress + 10).coerceAtMost(100)
                viewModel.updateProgress(userId, courseId, newProgress)

                if (newProgress > enrollment.progress) {
                    userViewModel.addXp(userId, 10)
                    Toast.makeText(requireContext(), "Lesson complete! +10 XP", Toast.LENGTH_SHORT).show()
                }

                if (newProgress == 100 && enrollment.progress < 100) {
                    userViewModel.addXp(userId, 200)
                    Toast.makeText(requireContext(), "Course completed! +200 XP", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.btnCancelEnrollment.setOnClickListener {
            if (courseId == -1 || userId == -1) return@setOnClickListener
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Cancel Enrollment")
                .setMessage("Do you want to remove this course from your enrolled list?")
                .setPositiveButton("Cancel Enrollment") { _, _ ->
                    viewModel.cancelEnrollment(userId, courseId)
                    Toast.makeText(requireContext(), "Enrollment cancelled", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.dashboardFragment)
                }
                .setNegativeButton("Keep Course", null)
                .show()
        }
    }

    private fun showRatingBottomSheet(userId: Int, courseId: Int) {
        val dialog = BottomSheetDialog(requireContext())
        val content = layoutInflater.inflate(R.layout.bottom_sheet_course_rating, null)
        val ratingBar = content.findViewById<RatingBar>(R.id.ratingBarCourse)

        content.findViewById<View>(R.id.btnSubmitCourseRating).setOnClickListener {
            val ratingValue = ratingBar.rating.coerceAtLeast(1f)
            viewModel.submitCourseRating(userId, courseId, ratingValue)
            Toast.makeText(requireContext(), "Thanks for rating this course!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.setContentView(content)
        dialog.setCancelable(false)
        dialog.show()
        ratingBottomSheet = dialog
    }

    private fun setupVideoPlayer() {
        try {
            // Build the URI path to the video in raw resources
            val videoUri = android.net.Uri.parse("android.resource://${requireContext().packageName}/${R.raw.learn_bridge_intro}")

            // Set video URI
            binding.videoPlayerView.setVideoURI(videoUri)

            // Setup media controller
            val mediaController = MediaController(requireContext())
            mediaController.setAnchorView(binding.videoPlayerView)
            binding.videoPlayerView.setMediaController(mediaController)

            // Start video when ready
            binding.videoPlayerView.setOnPreparedListener { mp ->
                mp.setLooping(false)
                binding.videoPlayerView.start()
            }

            // Handle completion
            binding.videoPlayerView.setOnCompletionListener {
                Toast.makeText(requireContext(), "Video completed!", Toast.LENGTH_SHORT).show()
            }

            // Handle errors
            binding.videoPlayerView.setOnErrorListener { _, what, extra ->
                Toast.makeText(requireContext(), "Error loading video", Toast.LENGTH_SHORT).show()
                false
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Could not load video", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateProgressUI(progress: Int) {
        binding.pbPlayerProgress.progress = progress
        binding.tvPlayerProgressPercent.text = "$progress% complete"

        if (progress == 100) {
            binding.btnMarkComplete.visibility = View.GONE
            binding.tvCompletionStatus.visibility = View.VISIBLE
        } else {
            binding.btnMarkComplete.visibility = View.VISIBLE
            binding.tvCompletionStatus.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        // Release video player resources
        try {
            if (binding.videoPlayerView.isPlaying) {
                binding.videoPlayerView.stopPlayback()
            }
        } catch (e: Exception) {
            // Handle any errors during cleanup
        }

        ratingBottomSheet?.dismiss()
        ratingBottomSheet = null
        super.onDestroyView()
        _binding = null
    }
}
