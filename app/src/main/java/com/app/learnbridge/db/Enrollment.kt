package com.app.learnbridge.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "enrollments",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Course::class,
            parentColumns = ["id"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Enrollment(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val courseId: Int,
    val progress: Int = 0,
    val hasRated: Boolean = false
)
