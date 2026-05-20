package com.app.learnbridge.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val category: String,
    val level: String,
    val imageUrl: String,
    val isPremium: Boolean = false,
    val curriculum: String = "",
    val durationHours: Int = 1,
    val ratingTotal: Float = 0f,
    val ratingCount: Int = 0
) {
    val averageRating: Float
        get() = if (ratingCount > 0) ratingTotal / ratingCount else 0f
}
