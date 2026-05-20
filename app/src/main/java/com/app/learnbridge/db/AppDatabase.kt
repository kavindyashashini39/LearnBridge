package com.app.learnbridge.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [User::class, Course::class, Enrollment::class, Subscription::class, Transaction::class], version = 7, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun courseDao(): CourseDao
    abstract fun enrollmentDao(): EnrollmentDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "learnbridge_database"
                )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                populateDatabase(database)
                            }
                        }
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                ensureAdminUser(database)
                            }
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateDatabase(db: AppDatabase) {
            val courseDao = db.courseDao()
            val userDao = db.userDao()

            // Seed Admin
            userDao.insertUser(User(name = "Admin", email = "admin@learnbridge.com", password = "admin", isAdmin = true))

            val courses = listOf(
                Course(
                    title = "Intro to Android",
                    description = "Learn the basics of Android development with Kotlin.",
                    category = "IT",
                    level = "Beginner",
                    imageUrl = "https://example.com/android.jpg",
                    isPremium = false,
                    curriculum = "Kotlin Basics\nAndroid Studio Setup\nLayouts and Navigation",
                    durationHours = 10
                ),
                Course(
                    title = "Advanced UI Design",
                    description = "Master complex layouts and animations in Compose.",
                    category = "IT",
                    level = "Advanced",
                    imageUrl = "https://example.com/ui.jpg",
                    isPremium = true,
                    curriculum = "Design Systems\nAdvanced Components\nPrototype to Production",
                    durationHours = 14
                ),
                Course(
                    title = "Data Science with Python",
                    description = "Analyze data and build machine learning models.",
                    category = "IT",
                    level = "Intermediate",
                    imageUrl = "https://example.com/ds.jpg",
                    isPremium = true,
                    curriculum = "Python Refresher\nPandas and Numpy\nIntro to ML",
                    durationHours = 16
                ),
                Course(
                    title = "Teaching Fundamentals",
                    description = "Learn learner-centered teaching strategies.",
                    category = "Teaching",
                    level = "Beginner",
                    imageUrl = "https://example.com/teach.jpg",
                    isPremium = false,
                    curriculum = "Teaching Methods\nClassroom Planning\nAssessment Basics",
                    durationHours = 8
                ),
                Course(
                    title = "Healthcare Essentials",
                    description = "Understand healthcare systems and patient communication.",
                    category = "Health",
                    level = "Beginner",
                    imageUrl = "https://example.com/health.jpg",
                    isPremium = true,
                    curriculum = "Healthcare Systems\nPatient Care Basics\nSafety Protocols",
                    durationHours = 12
                )
            )
            courses.forEach { courseDao.insertCourse(it) }
        }

        private suspend fun ensureAdminUser(db: AppDatabase) {
            val userDao = db.userDao()
            // Check if admin user already exists
            val adminUser = userDao.getUserByEmail("admin@learnbridge.com")
            if (adminUser == null) {
                // Create admin user if it doesn't exist
                userDao.insertUser(User(name = "Admin", email = "admin@learnbridge.com", password = "admin", isAdmin = true))
            }
        }
    }
}
