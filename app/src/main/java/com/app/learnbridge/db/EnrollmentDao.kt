package com.app.learnbridge.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EnrollmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enrollUser(enrollment: Enrollment)

    @Query("SELECT * FROM enrollments WHERE userId = :userId")
    fun getEnrollmentsByUser(userId: Int): Flow<List<Enrollment>>

    @Query("SELECT * FROM enrollments WHERE userId = :userId AND courseId = :courseId LIMIT 1")
    suspend fun getEnrollment(userId: Int, courseId: Int): Enrollment?

    @Query("UPDATE enrollments SET progress = :progress WHERE userId = :userId AND courseId = :courseId")
    suspend fun updateProgress(userId: Int, courseId: Int, progress: Int)

    @Query("UPDATE enrollments SET hasRated = 1 WHERE userId = :userId AND courseId = :courseId")
    suspend fun markCourseRated(userId: Int, courseId: Int)

    @Query("DELETE FROM enrollments WHERE userId = :userId AND courseId = :courseId")
    suspend fun cancelEnrollment(userId: Int, courseId: Int)
}
