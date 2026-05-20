package com.app.learnbridge.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val email: String,
    val password: String,
    val interests: String? = null,
    val goal: String? = null,
    val experience: String? = null,
    val careerStage: String? = null,
    val hobbies: String? = null,
    val xp: Int = 0,
    val subscription: String = "Free",
    val isAdmin: Boolean = false,
    val surveyCompleted: Boolean = false,  // Flag to track if user completed survey (one-time)
    val profilePhotoPath: String? = null  // Path to locally saved profile photo
)
