package com.app.learnbridge.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.app.learnbridge.LearnBridgeApplication
import com.app.learnbridge.databinding.FragmentAddEditCourseBinding
import com.app.learnbridge.db.Course
import com.app.learnbridge.viewmodel.CourseViewModel
import com.bumptech.glide.Glide
import java.io.File
import java.io.FileOutputStream

class AddEditCourseFragment : Fragment() {

    private var _binding: FragmentAddEditCourseBinding? = null
    private val binding get() = _binding!!

    private val args: AddEditCourseFragmentArgs by navArgs()
    private val courseViewModel: CourseViewModel by viewModels {
        CourseViewModel.CourseViewModelFactory((requireActivity().application as LearnBridgeApplication).courseRepository)
    }

    private var existingCourse: Course? = null
    private var selectedLocalImageUri: Uri? = null

    private val imagePicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            selectedLocalImageUri = uri
            requireContext().contentResolver.takePersistableUriPermission(
                uri,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            Glide.with(this).load(uri).into(binding.ivCoursePreview)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditCourseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val courseId = args.courseId
        if (courseId != -1) {
            binding.tvTitle.text = "Edit Course"
            courseViewModel.getCourseById(courseId).observe(viewLifecycleOwner) { course ->
                course?.let {
                    existingCourse = it
                    binding.etCourseTitle.setText(it.title)
                    binding.etCategory.setText(it.category)
                    binding.etLevel.setText(it.level)
                    binding.etDescription.setText(it.description)
                    binding.etCurriculum.setText(it.curriculum)
                    binding.etDurationHours.setText(it.durationHours.toString())
                    binding.etImageUrl.setText(it.imageUrl)
                    binding.cbIsPremium.isChecked = it.isPremium
                    loadPreviewImage(it.imageUrl)
                }
            }
        } else {
            binding.tvTitle.text = "Add New Course"
        }

        binding.btnChooseImage.setOnClickListener {
            imagePicker.launch(arrayOf("image/*"))
        }

        binding.btnSave.setOnClickListener {
            saveCourse()
        }
    }

    private fun saveCourse() {
        val title = binding.etCourseTitle.text.toString().trim()
        val category = binding.etCategory.text.toString().trim()
        val level = binding.etLevel.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val curriculum = binding.etCurriculum.text.toString().trim()
        val durationHours = binding.etDurationHours.text.toString().trim().toIntOrNull() ?: 0
        val imageUrlInput = binding.etImageUrl.text.toString().trim()
        val isPremium = binding.cbIsPremium.isChecked

        if (title.isEmpty() || category.isEmpty() || level.isEmpty() || description.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }
        if (durationHours <= 0) {
            Toast.makeText(requireContext(), "Duration hours must be greater than 0", Toast.LENGTH_SHORT).show()
            return
        }

        val savedLocalImagePath = selectedLocalImageUri?.let { copyImageToInternalStorage(it) }
        val finalImagePath = savedLocalImagePath ?: imageUrlInput.ifEmpty { existingCourse?.imageUrl ?: "" }

        val course = if (existingCourse != null) {
            existingCourse!!.copy(
                title = title,
                category = category,
                level = level,
                description = description,
                curriculum = curriculum,
                durationHours = durationHours,
                imageUrl = finalImagePath,
                isPremium = isPremium
            )
        } else {
            Course(
                title = title,
                category = category,
                level = level,
                description = description,
                curriculum = curriculum,
                durationHours = durationHours,
                imageUrl = finalImagePath,
                isPremium = isPremium
            )
        }

        if (existingCourse != null) {
            courseViewModel.updateCourse(course)
            Toast.makeText(requireContext(), "Course updated", Toast.LENGTH_SHORT).show()
        } else {
            courseViewModel.addCourse(course)
            Toast.makeText(requireContext(), "Course added", Toast.LENGTH_SHORT).show()
        }
        findNavController().popBackStack()
    }

    private fun copyImageToInternalStorage(sourceUri: Uri): String? {
        return try {
            val imagesDir = File(requireContext().filesDir, "course_images")
            if (!imagesDir.exists()) imagesDir.mkdirs()

            val fileName = "course_${System.currentTimeMillis()}.jpg"
            val destFile = File(imagesDir, fileName)

            requireContext().contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            destFile.absolutePath
        } catch (_: Exception) {
            Toast.makeText(requireContext(), "Failed to save local image", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun loadPreviewImage(imagePath: String) {
        val model: Any = if (imagePath.startsWith("/")) {
            File(imagePath)
        } else {
            imagePath.toUri()
        }
        Glide.with(this)
            .load(model)
            .into(binding.ivCoursePreview)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
