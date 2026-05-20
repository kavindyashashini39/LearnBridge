package com.app.learnbridge.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: Course)

    @Query("SELECT * FROM courses")
    fun getAllCourses(): Flow<List<Course>>

    @Query("SELECT * FROM courses")
    suspend fun getAllCoursesOnce(): List<Course>

    @Query("SELECT * FROM courses WHERE category = :category")
    fun getCoursesByCategory(category: String): Flow<List<Course>>

    @Query("SELECT * FROM courses WHERE id = :courseId")
    suspend fun getCourseById(courseId: Int): Course?

    @Update
    suspend fun updateCourse(course: Course)

    @Delete
    suspend fun deleteCourse(course: Course)

    @Query("SELECT * FROM courses WHERE title LIKE '%' || :searchQuery || '%'")
    fun searchCourses(searchQuery: String): Flow<List<Course>>

    @Query("UPDATE courses SET ratingTotal = ratingTotal + :ratingValue, ratingCount = ratingCount + 1 WHERE id = :courseId")
    suspend fun addCourseRating(courseId: Int, ratingValue: Float)
}
