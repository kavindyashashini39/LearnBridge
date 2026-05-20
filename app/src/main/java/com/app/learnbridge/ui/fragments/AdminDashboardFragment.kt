package com.app.learnbridge.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.learnbridge.LearnBridgeApplication
import com.app.learnbridge.databinding.FragmentAdminDashboardBinding
import com.app.learnbridge.ui.adapters.AdminCourseAdapter
import com.app.learnbridge.viewmodel.CourseViewModel

class AdminDashboardFragment : Fragment() {

    private var _binding: FragmentAdminDashboardBinding? = null
    private val binding get() = _binding!!

    private val courseViewModel: CourseViewModel by viewModels {
        CourseViewModel.CourseViewModelFactory((requireActivity().application as LearnBridgeApplication).courseRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = AdminCourseAdapter(
            onEditClick = { course ->
                val action = AdminDashboardFragmentDirections.actionAdminDashboardFragmentToAddEditCourseFragment(
                    courseId = course.id,
                    title = "Edit Course"
                )
                findNavController().navigate(action)
            },
            onDeleteClick = { course ->
                showDeleteConfirmation(course)
            }
        )

        binding.rvAdminCourses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAdminCourses.adapter = adapter

        courseViewModel.allCourses.observe(viewLifecycleOwner) { courses ->
            adapter.submitList(courses)
        }

        binding.btnAddCourse.setOnClickListener {
            val action = AdminDashboardFragmentDirections.actionAdminDashboardFragmentToAddEditCourseFragment(
                courseId = -1,
                title = "Add Course"
            )
            findNavController().navigate(action)
        }
    }

    private fun showDeleteConfirmation(course: com.app.learnbridge.db.Course) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Course")
            .setMessage("Are you sure you want to delete ${course.title}?")
            .setPositiveButton("Delete") { _, _ ->
                courseViewModel.deleteCourse(course)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

