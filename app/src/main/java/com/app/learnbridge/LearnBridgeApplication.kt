package com.app.learnbridge

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.app.learnbridge.db.AppDatabase
import com.app.learnbridge.repository.CourseRepository
import com.app.learnbridge.repository.SubscriptionRepository
import com.app.learnbridge.repository.TransactionRepository
import com.app.learnbridge.repository.UserRepository

class LearnBridgeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    val database by lazy { AppDatabase.getDatabase(this) }
    val userRepository by lazy { UserRepository(database.userDao()) }
    val courseRepository by lazy { CourseRepository(database.courseDao(), database.enrollmentDao()) }
    val subscriptionRepository by lazy { SubscriptionRepository(database.subscriptionDao(), database.transactionDao()) }
    val transactionRepository by lazy { TransactionRepository(database.transactionDao()) }
}
