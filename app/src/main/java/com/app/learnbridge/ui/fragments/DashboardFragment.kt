package com.app.learnbridge.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.learnbridge.LearnBridgeApplication
import com.app.learnbridge.R
import com.app.learnbridge.ui.adapters.EnrollmentAdapter
import com.app.learnbridge.util.SessionManager
import com.app.learnbridge.viewmodel.CourseViewModel
import com.app.learnbridge.viewmodel.UserViewModel

class DashboardFragment : Fragment() {

    private val courseViewModel: CourseViewModel by viewModels {
        CourseViewModel.CourseViewModelFactory((requireActivity().application as LearnBridgeApplication).courseRepository)
    }
    private val userViewModel: UserViewModel by viewModels {
        UserViewModel.UserViewModelFactory((requireActivity().application as LearnBridgeApplication).userRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val userId = SessionManager(requireContext()).getUserId()
        val rvEnrollments = view.findViewById<RecyclerView>(R.id.rvEnrollments)
        val tvTotalXp = view.findViewById<TextView>(R.id.tvTotalXp)
        val tvActiveCourses = view.findViewById<TextView>(R.id.tvActiveCourses)
        val tvNoEnrollments = view.findViewById<TextView>(R.id.tvNoEnrollments)

        rvEnrollments.layoutManager = LinearLayoutManager(requireContext())

        userViewModel.getUserById(userId).observe(viewLifecycleOwner) { user ->
            tvTotalXp.text = "Total XP\n${user?.xp ?: 0}"
        }

        courseViewModel.allCourses.observe(viewLifecycleOwner) { allCourses ->
            courseViewModel.getEnrolledCourses(userId).observe(viewLifecycleOwner) { enrollments ->
                if (enrollments.isEmpty()) {
                    tvNoEnrollments.visibility = View.VISIBLE
                    rvEnrollments.visibility = View.GONE
                    tvActiveCourses.text = "Active Courses\n0"
                } else {
                    tvNoEnrollments.visibility = View.GONE
                    rvEnrollments.visibility = View.VISIBLE
                    tvActiveCourses.text = "Active Courses\n${enrollments.size}"
                    
                    val adapter = EnrollmentAdapter(allCourses) { enrollment ->
                        val bundle = Bundle().apply {
                            putInt("courseId", enrollment.courseId)
                        }
                        findNavController().navigate(R.id.action_dashboardFragment_to_coursePlayerFragment, bundle)
                    }
                    rvEnrollments.adapter = adapter
                    adapter.submitList(enrollments)
                }
            }
        }

        view.findViewById<View>(R.id.btnDashboardCourses).setOnClickListener {
            findNavController().navigate(R.id.recommendationsFragment)
        }
    }
}
