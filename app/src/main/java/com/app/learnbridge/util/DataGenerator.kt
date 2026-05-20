package com.app.learnbridge.util

import com.app.learnbridge.db.Course

object DataGenerator {
    fun getSampleCourses(): List<Course> {
        return listOf(
            Course(
                title = "Introduction to UI/UX Design",
                description = "Learn the basics of User Interface and User Experience design.",
                category = "Design",
                level = "Beginner",
                imageUrl = "https://images.unsplash.com/photo-1586717791821-3f44a563dc4c",
                curriculum = "Design Basics\nWireframes\nPrototyping",
                durationHours = 9
            ),
            Course(
                title = "Advanced Kotlin for Android",
                description = "Master Kotlin programming language for modern Android development.",
                category = "Development",
                level = "Advanced",
                imageUrl = "https://images.unsplash.com/photo-1517694712202-14dd9538aa97",
                curriculum = "Advanced Kotlin\nCoroutines\nArchitecture Patterns",
                durationHours = 15
            ),
            Course(
                title = "Data Science with Python",
                description = "Dive into data analysis, visualization, and machine learning.",
                category = "Data Science",
                level = "Intermediate",
                imageUrl = "https://images.unsplash.com/photo-1551288049-bebda4e38f71",
                curriculum = "Data Cleaning\nVisualization\nML Foundations",
                durationHours = 14
            ),
            Course(
                title = "Digital Marketing Essentials",
                description = "Understand SEO, SEM, and social media marketing strategies.",
                category = "Business",
                level = "Beginner",
                imageUrl = "https://images.unsplash.com/photo-1460925895917-afdab827c52f",
                curriculum = "SEO Basics\nSocial Media\nCampaign Analytics",
                durationHours = 7
            ),
            Course(
                title = "Artificial Intelligence Fundamentals",
                description = "Explore the core concepts of AI and its applications.",
                category = "AI",
                level = "Beginner",
                imageUrl = "https://images.unsplash.com/photo-1507146426996-ef05306b995a",
                curriculum = "AI Concepts\nUse Cases\nEthics",
                durationHours = 11
            )
        )
    }
}
