package com.app.learnbridge.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.learnbridge.LearnBridgeApplication
import androidx.navigation.fragment.findNavController
import com.app.learnbridge.R
import com.app.learnbridge.db.Course
import com.app.learnbridge.db.Enrollment
import com.app.learnbridge.util.SessionManager
import com.app.learnbridge.viewmodel.CourseViewModel

class CertificatesFragment : Fragment() {

    private val courseViewModel: CourseViewModel by viewModels {
        CourseViewModel.CourseViewModelFactory((requireActivity().application as LearnBridgeApplication).courseRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_certificates, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.tvEarnTab).setOnClickListener {
            findNavController().navigate(R.id.earnFragment)
        }

        val rvCertificates = view.findViewById<RecyclerView>(R.id.rvCertificates)
        val tvHeader = view.findViewById<TextView>(R.id.tvCertificateHeader)
        val tvEmpty = view.findViewById<TextView>(R.id.tvEmptyCertificates)

        rvCertificates.layoutManager = LinearLayoutManager(requireContext())
        val adapter = CertificateAdapter()
        rvCertificates.adapter = adapter

        val userId = SessionManager(requireContext()).getUserId()

        courseViewModel.allCourses.observe(viewLifecycleOwner) { courses ->
            courseViewModel.getEnrolledCourses(userId).observe(viewLifecycleOwner) { enrollments ->
                val completed = enrollments.filter { it.progress >= 100 }
                val items = completed.mapNotNull { enrollment ->
                    courses.firstOrNull { it.id == enrollment.courseId }?.let { course ->
                        CertificateItem(course, enrollment.progress)
                    }
                }

                tvHeader.text = "Your Certificates (${items.size})"
                tvEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
                rvCertificates.visibility = if (items.isEmpty()) View.GONE else View.VISIBLE
                adapter.submitList(items)
            }
        }
    }

    private data class CertificateItem(
        val course: Course,
        val progress: Int
    )

    private class CertificateAdapter : RecyclerView.Adapter<CertificateViewHolder>() {
        private var items: List<CertificateItem> = emptyList()

        fun submitList(newItems: List<CertificateItem>) {
            items = newItems
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CertificateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_certificate, parent, false)
            return CertificateViewHolder(view)
        }

        override fun onBindViewHolder(holder: CertificateViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size
    }

    private class CertificateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title = itemView.findViewById<TextView>(R.id.tvCertificateTitle)
        private val meta = itemView.findViewById<TextView>(R.id.tvCertificateMeta)

        fun bind(item: CertificateItem) {
            title.text = item.course.title
            meta.text = "Completion: ${item.progress}%"
        }
    }
}
