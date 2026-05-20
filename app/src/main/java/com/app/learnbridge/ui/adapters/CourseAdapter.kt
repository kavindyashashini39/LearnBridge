package com.app.learnbridge.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.learnbridge.databinding.ItemCourseBinding
import com.app.learnbridge.db.Course
import com.app.learnbridge.util.ImageLoader

class CourseAdapter(private val onCourseClick: (Course) -> Unit) :
    ListAdapter<Course, CourseAdapter.CourseViewHolder>(CourseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val binding = ItemCourseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CourseViewHolder(private val binding: ItemCourseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(course: Course) {
            binding.tvCourseTitle.text = course.title
            binding.tvCourseCategory.text = course.category
            binding.tvCourseLevel.text = course.level
            binding.tvCourseDuration.text = "${course.durationHours} hrs"
            binding.tvCourseRating.text = String.format("%.1f ★", course.averageRating)
            
            // Load course image with proper fallback logic
            ImageLoader.loadCourseImage(binding.ivCourseImage, course.imageUrl)

            binding.root.setOnClickListener {
                onCourseClick(course)
            }
        }
    }

    class CourseDiffCallback : DiffUtil.ItemCallback<Course>() {
        override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem == newItem
        }
    }
}
