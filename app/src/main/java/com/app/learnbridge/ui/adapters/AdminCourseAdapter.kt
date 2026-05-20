package com.app.learnbridge.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.learnbridge.databinding.ItemAdminCourseBinding
import com.app.learnbridge.db.Course

class AdminCourseAdapter(
    private val onEditClick: (Course) -> Unit,
    private val onDeleteClick: (Course) -> Unit
) : ListAdapter<Course, AdminCourseAdapter.AdminCourseViewHolder>(CourseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminCourseViewHolder {
        val binding = ItemAdminCourseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdminCourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdminCourseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AdminCourseViewHolder(private val binding: ItemAdminCourseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(course: Course) {
            binding.tvCourseTitle.text = course.title
            binding.tvCourseCategory.text = course.category
            binding.btnEdit.setOnClickListener { onEditClick(course) }
            binding.btnDelete.setOnClickListener { onDeleteClick(course) }
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
