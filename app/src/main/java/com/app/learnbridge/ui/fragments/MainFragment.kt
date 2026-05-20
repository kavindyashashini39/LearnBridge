package com.app.learnbridge.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import android.graphics.Color
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.app.learnbridge.LearnBridgeApplication
import com.app.learnbridge.R
import com.app.learnbridge.db.Course
import com.app.learnbridge.ui.adapters.CourseAdapter
import com.app.learnbridge.util.SessionManager
import com.app.learnbridge.util.ImageLoader
import com.app.learnbridge.viewmodel.CourseViewModel
import com.app.learnbridge.viewmodel.UserViewModel

class MainFragment : Fragment() {

    private val courseViewModel: CourseViewModel by viewModels {
        CourseViewModel.CourseViewModelFactory((requireActivity().application as LearnBridgeApplication).courseRepository)
    }
    private val userViewModel: UserViewModel by viewModels {
        UserViewModel.UserViewModelFactory((requireActivity().application as LearnBridgeApplication).userRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager(requireContext())
        val llAuthButtons = view.findViewById<View>(R.id.llAuthButtons)
        val flProfileContainer = view.findViewById<View>(R.id.flProfileContainer)
        val ivProfile = view.findViewById<ImageView>(R.id.ivProfile)
        val rvRecommendations = view.findViewById<RecyclerView>(R.id.rvForYouRecommendations)
        val tvNoRecommendations = view.findViewById<TextView>(R.id.tvNoForYouRecommendations)

        if (sessionManager.isLoggedIn()) {
            llAuthButtons.visibility = View.GONE
            flProfileContainer.visibility = View.VISIBLE

            // Load user profile photo and apply glow border if pro
            val userId = sessionManager.getUserId()
            val profileCardView = view.findViewById<MaterialCardView>(R.id.profileCardView)

            userViewModel.getUserById(userId).observe(viewLifecycleOwner) { user ->
                user?.let {
                    if (!it.profilePhotoPath.isNullOrEmpty()) {
                        ImageLoader.loadCourseImage(ivProfile, it.profilePhotoPath, isCircle = true)
                    }

                    // Apply glowing border if user is premium subscriber
                    if (it.subscription == "Premium") {
                        profileCardView.strokeWidth = resources.getDimensionPixelSize(R.dimen.pro_glow_stroke_width)
                        profileCardView.setCardBackgroundColor(Color.WHITE)
                    } else {
                        profileCardView.strokeWidth = 0
                        profileCardView.setCardBackgroundColor(Color.WHITE)
                    }
                }
            }
        } else {
            llAuthButtons.visibility = View.VISIBLE
            flProfileContainer.visibility = View.GONE
        }

        setupForYouRecommendations(sessionManager, rvRecommendations, tvNoRecommendations)

        flProfileContainer.setOnClickListener {
            findNavController().navigate(R.id.profileFragment)
        }

        view.findViewById<View>(R.id.tvHomeLogin).setOnClickListener {
            findNavController().navigate(R.id.authFragment)
        }
        view.findViewById<View>(R.id.tvHomeSignUp).setOnClickListener {
            findNavController().navigate(R.id.authFragment)
        }
        view.findViewById<View>(R.id.cardCertificates).setOnClickListener {
            findNavController().navigate(R.id.certificatesFragment)
        }
        view.findViewById<View>(R.id.cardBuildCareer).setOnClickListener {
            findNavController().navigate(R.id.recommendationsFragment)
        }
        view.findViewById<View>(R.id.cardEarn).setOnClickListener {
            findNavController().navigate(R.id.earnFragment)
        }
        view.findViewById<View>(R.id.btnRecommendations).setOnClickListener {
            findNavController().navigate(R.id.recommendationsFragment)
        }
        view.findViewById<View>(R.id.llSearchBox).setOnClickListener {
            val bundle = Bundle().apply { putBoolean("focusSearch", true) }
            findNavController().navigate(R.id.recommendationsFragment, bundle)
        }
    }

    private fun setupForYouRecommendations(
        sessionManager: SessionManager,
        recyclerView: RecyclerView,
        emptyText: TextView
    ) {
        val adapter = CourseAdapter { course ->
            val bundle = Bundle().apply { putInt("courseId", course.id) }
            findNavController().navigate(R.id.courseDetailsFragment, bundle)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter

        val userId = sessionManager.getUserId()
        if (!sessionManager.isLoggedIn() || userId == -1) {
            courseViewModel.allCourses.observe(viewLifecycleOwner) { allCourses ->
                val fallback = allCourses.take(4)
                adapter.submitList(fallback)
                emptyText.visibility = if (fallback.isEmpty()) View.VISIBLE else View.GONE
            }
            return
        }

        userViewModel.getUserById(userId).observe(viewLifecycleOwner) { user ->
            courseViewModel.allCourses.observe(viewLifecycleOwner) { allCourses ->
                val recommendationKeywords = buildList {
                    addAll(splitKeywords(user?.goal))
                    addAll(splitKeywords(user?.experience))
                    addAll(splitKeywords(user?.interests))
                    addAll(splitKeywords(user?.careerStage))
                    addAll(splitKeywords(user?.hobbies))
                }.distinct()

                val recommended = if (recommendationKeywords.isEmpty()) {
                    allCourses.take(6)
                } else {
                    allCourses
                        .map { course ->
                            val text = "${course.title} ${course.category} ${course.description}".lowercase()
                            val score = recommendationKeywords.count { text.contains(it) }
                            course to score
                        }
                        .filter { it.second > 0 }
                        .sortedByDescending { it.second }
                        .map { it.first }
                        .take(6)
                        .ifEmpty { allCourses.take(6) }
                }

                adapter.submitList(recommended)
                emptyText.visibility = if (recommended.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun splitKeywords(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return value.lowercase()
            .split(",", "/", "&", " ")
            .map { it.trim() }
            .filter { it.length > 2 }
    }
}
