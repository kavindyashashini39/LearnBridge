package com.app.learnbridge.viewmodel

import androidx.lifecycle.*
import com.app.learnbridge.db.Course
import com.app.learnbridge.db.Enrollment
import com.app.learnbridge.repository.CourseRepository
import kotlinx.coroutines.launch

class CourseViewModel(private val repository: CourseRepository) : ViewModel() {

    val allCourses: LiveData<List<Course>> = repository.getAllCourses().asLiveData()

    fun getCourseById(courseId: Int): LiveData<Course?> {
        val result = MutableLiveData<Course?>()
        viewModelScope.launch {
            result.value = repository.getCourseById(courseId)
        }
        return result
    }

    fun enroll(userId: Int, courseId: Int) {
        viewModelScope.launch {
            val enrollment = Enrollment(userId = userId, courseId = courseId)
            repository.enrollUser(enrollment)
        }
    }

    fun addCourse(course: Course) {
        viewModelScope.launch {
            repository.insertCourse(course)
        }
    }

    fun updateCourse(course: Course) {
        viewModelScope.launch {
            repository.updateCourse(course)
        }
    }

    fun deleteCourse(course: Course) {
        viewModelScope.launch {
            repository.deleteCourse(course)
        }
    }

    fun isEnrolled(userId: Int, courseId: Int): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            result.value = repository.getEnrollment(userId, courseId) != null
        }
        return result
    }

    fun getEnrolledCourses(userId: Int): LiveData<List<Enrollment>> {
        return repository.getEnrollmentsByUser(userId).asLiveData()
    }

    fun getEnrollment(userId: Int, courseId: Int): LiveData<Enrollment?> {
        val result = MutableLiveData<Enrollment?>()
        viewModelScope.launch {
            result.postValue(repository.getEnrollment(userId, courseId))
        }
        return result
    }

    fun updateProgress(userId: Int, courseId: Int, progress: Int) {
        viewModelScope.launch {
            repository.updateProgress(userId, courseId, progress)
            // To trigger UI update, we might need a way to refresh the enrollment LiveData
            // or use a Flow that observes the DB.
        }
    }

    fun cancelEnrollment(userId: Int, courseId: Int) {
        viewModelScope.launch {
            repository.cancelEnrollment(userId, courseId)
        }
    }

    fun submitCourseRating(userId: Int, courseId: Int, ratingValue: Float) {
        viewModelScope.launch {
            repository.submitCourseRating(userId, courseId, ratingValue)
        }
    }

    fun isPremiumUser(userId: Int, subscriptionRepository: com.app.learnbridge.repository.SubscriptionRepository): LiveData<Boolean> {
        return subscriptionRepository.getLatestSubscription(userId).asLiveData().map { it?.plan == "Premium" }
    }

    class CourseViewModelFactory(private val repository: CourseRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CourseViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CourseViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
