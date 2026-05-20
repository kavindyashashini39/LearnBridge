package com.app.learnbridge.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.learnbridge.R
import com.app.learnbridge.db.Course
import com.app.learnbridge.db.Enrollment

class EnrollmentAdapter(
    private val courses: List<Course>,
    private val onEnrollmentClick: (Enrollment) -> Unit
) : ListAdapter<Enrollment, EnrollmentAdapter.EnrollmentViewHolder>(EnrollmentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnrollmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_enrollment, parent, false)
        return EnrollmentViewHolder(view, onEnrollmentClick)
    }

    override fun onBindViewHolder(holder: EnrollmentViewHolder, position: Int) {
        val enrollment = getItem(position)
        val course = courses.find { it.id == enrollment.courseId }
        holder.bind(enrollment, course)
    }

    class EnrollmentViewHolder(
        itemView: View,
        private val onEnrollmentClick: (Enrollment) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val ivCourseIcon: ImageView = itemView.findViewById(R.id.ivCourseIcon)
        private val tvCourseTitle: TextView = itemView.findViewById(R.id.tvCourseTitle)
        private val pbProgress: ProgressBar = itemView.findViewById(R.id.pbProgress)
        private val tvProgressPercent: TextView = itemView.findViewById(R.id.tvProgressPercent)

        fun bind(enrollment: Enrollment, course: Course?) {
            tvCourseTitle.text = course?.title ?: "Unknown Course"
            pbProgress.progress = enrollment.progress
            tvProgressPercent.text = "${enrollment.progress}% complete"
            
            itemView.setOnClickListener {
                onEnrollmentClick(enrollment)
            }
        }
    }

    class EnrollmentDiffCallback : DiffUtil.ItemCallback<Enrollment>() {
        override fun areItemsTheSame(oldItem: Enrollment, newItem: Enrollment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Enrollment, newItem: Enrollment): Boolean {
            return oldItem == newItem
        }
    }
}
