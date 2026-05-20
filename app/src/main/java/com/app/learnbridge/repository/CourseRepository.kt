package com.app.learnbridge.repository

import com.app.learnbridge.db.Course
import com.app.learnbridge.db.CourseDao
import com.app.learnbridge.db.Enrollment
import com.app.learnbridge.db.EnrollmentDao
import kotlinx.coroutines.flow.Flow

class CourseRepository(private val courseDao: CourseDao, private val enrollmentDao: EnrollmentDao) {

    fun getAllCourses(): Flow<List<Course>> {
        return courseDao.getAllCourses()
    }

    fun getCoursesByCategory(category: String): Flow<List<Course>> {
        return courseDao.getCoursesByCategory(category)
    }

    suspend fun getCourseById(courseId: Int): Course? {
        return courseDao.getCourseById(courseId)
    }

    fun searchCourses(query: String): Flow<List<Course>> {
        return courseDao.searchCourses(query)
    }

    suspend fun insertCourse(course: Course) {
        courseDao.insertCourse(course)
    }

    suspend fun updateCourse(course: Course) {
        courseDao.updateCourse(course)
    }

    suspend fun deleteCourse(course: Course) {
        courseDao.deleteCourse(course)
    }

    suspend fun seedIfEmpty(courses: List<Course>) {
        val existingCourses = courseDao.getAllCoursesOnce()
        if (existingCourses.isEmpty()) {
            courses.forEach { courseDao.insertCourse(it) }
        }
    }

    suspend fun enrollUser(enrollment: Enrollment) {
        enrollmentDao.enrollUser(enrollment)
    }

    fun getEnrollmentsByUser(userId: Int): Flow<List<Enrollment>> {
        return enrollmentDao.getEnrollmentsByUser(userId)
    }

    suspend fun getEnrollment(userId: Int, courseId: Int): Enrollment? {
        return enrollmentDao.getEnrollment(userId, courseId)
    }

    suspend fun updateProgress(userId: Int, courseId: Int, progress: Int) {
        enrollmentDao.updateProgress(userId, courseId, progress)
    }

    suspend fun cancelEnrollment(userId: Int, courseId: Int) {
        enrollmentDao.cancelEnrollment(userId, courseId)
    }

    suspend fun submitCourseRating(userId: Int, courseId: Int, ratingValue: Float) {
        courseDao.addCourseRating(courseId, ratingValue)
        enrollmentDao.markCourseRated(userId, courseId)
    }
}
