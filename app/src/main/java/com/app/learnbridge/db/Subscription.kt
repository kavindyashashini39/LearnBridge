package com.app.learnbridge.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "subscriptions",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Subscription(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val plan: String,
    val startDate: Long
)
