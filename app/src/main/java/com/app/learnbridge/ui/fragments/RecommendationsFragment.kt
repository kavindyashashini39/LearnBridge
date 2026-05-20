package com.app.learnbridge.ui.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.app.learnbridge.LearnBridgeApplication
import com.app.learnbridge.R
import com.app.learnbridge.databinding.FragmentRecommendationsBinding
import com.app.learnbridge.db.Course
import com.app.learnbridge.ui.adapters.CourseAdapter
import com.app.learnbridge.viewmodel.CourseViewModel
import com.app.learnbridge.viewmodel.UserViewModel
import com.app.learnbridge.util.SessionManager

class RecommendationsFragment : Fragment() {
    private var _binding: FragmentRecommendationsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CourseViewModel by viewModels {
        CourseViewModel.CourseViewModelFactory((requireActivity().application as LearnBridgeApplication).courseRepository)
    }
    private val userViewModel: UserViewModel by viewModels {
        UserViewModel.UserViewModelFactory((requireActivity().application as LearnBridgeApplication).userRepository)
    }

    private var allCourses: List<Course> = emptyList()
    private var currentQuery: String = ""
    private var currentCategory: String = "All"
    private var recommendationKeywords: List<String> = emptyList()
    private lateinit var courseAdapter: CourseAdapter
    private val filterViews = mutableMapOf<String, TextView>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecommendationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        courseAdapter = CourseAdapter { course ->
            val bundle = Bundle().apply { putInt("courseId", course.id) }
            findNavController().navigate(R.id.courseDetailsFragment, bundle)
        }

        binding.rvRecommendations.apply {
            adapter = courseAdapter
            layoutManager = GridLayoutManager(context, 2)
        }

        setupSearch()

        // Check if we should focus on search
        val focusSearch = arguments?.getBoolean("focusSearch", false) ?: false
        if (focusSearch) {
            binding.etSearch.requestFocus()
            // Show keyboard
            val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT)
        }

        viewModel.allCourses.observe(viewLifecycleOwner) { courses ->
            allCourses = courses
            renderDynamicCategories(courses)
            applyFilters()
        }

        val userId = SessionManager(requireContext()).getUserId()
        if (userId != -1) {
            userViewModel.getUserById(userId).observe(viewLifecycleOwner) { user ->
                recommendationKeywords = buildList {
                    addAll(splitKeywords(user?.goal))
                    addAll(splitKeywords(user?.experience))
                    addAll(splitKeywords(user?.interests))
                    addAll(splitKeywords(user?.careerStage))
                    addAll(splitKeywords(user?.hobbies))
                }.distinct()
                applyFilters()
            }
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentQuery = s.toString().trim()
                applyFilters()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun renderDynamicCategories(courses: List<Course>) {
        val categories = mutableListOf("All")
        categories.addAll(courses.map { it.category.trim() }.filter { it.isNotEmpty() }.distinct().sorted())

        val container = binding.llDynamicFilters
        container.removeAllViews()
        filterViews.clear()

        categories.forEachIndexed { index, category ->
            val chip = TextView(requireContext()).apply {
                text = category
                textSize = 12f
                setTextColor(android.graphics.Color.BLACK)
                setPadding(24, 8, 24, 8) // horizontal: 24px, vertical: 8px
                gravity = android.view.Gravity.CENTER
                background = if (category == currentCategory) {
                    requireContext().getDrawable(R.drawable.bg_survey_item_selected)
                } else {
                    requireContext().getDrawable(R.drawable.bg_white_rounded)
                }
                setOnClickListener {
                    currentCategory = category
                    updateFilterUI()
                    applyFilters()
                }
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    (36 * resources.displayMetrics.density).toInt()
                ).apply {
                    if (index > 0) marginStart = 10
                }
            }
            filterViews[category] = chip
            container.addView(chip)
        }

        if (!categories.contains(currentCategory)) {
            currentCategory = "All"
        }
        updateFilterUI()
    }

    private fun updateFilterUI() {
        filterViews.forEach { (category, view) ->
            view.setBackgroundResource(
                if (category == currentCategory) R.drawable.bg_survey_item_selected
                else R.drawable.bg_white_rounded
            )
        }
    }

    private fun applyFilters() {
        var filtered = allCourses
            .asSequence()
            .filter { course ->
                currentCategory == "All" || course.category.equals(currentCategory, ignoreCase = true)
            }
            .filter { course ->
                if (currentQuery.isBlank()) true
                else {
                    val q = currentQuery.lowercase()
                    course.title.lowercase().contains(q) ||
                        course.category.lowercase().contains(q) ||
                        course.description.lowercase().contains(q)
                }
            }
            .toList()

        if (currentCategory == "All" && currentQuery.isBlank() && recommendationKeywords.isNotEmpty()) {
            filtered = filtered
                .map { course ->
                    val text = "${course.title} ${course.category} ${course.description}".lowercase()
                    val score = recommendationKeywords.count { text.contains(it) }
                    course to score
                }
                .sortedByDescending { it.second }
                .map { it.first }
        }

        courseAdapter.submitList(filtered)
        binding.tvNoCourses.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        binding.rvRecommendations.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun splitKeywords(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return value.lowercase()
            .split(",", "/", "&", " ")
            .map { it.trim() }
            .filter { it.length > 2 }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
