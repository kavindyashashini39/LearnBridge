# 📱 LearnBridge Mobile Application – Development Plan

## 1. Overview
The LearnBridge mobile application is an offline-first Android app developed using Kotlin and SQLite (Room Database). The system provides a structured learning platform with features such as course browsing, enrollment, progress tracking, and subscription simulation.

---

## 2. Technology Stack
- **Language:** Kotlin
- **IDE:** Android Studio
- **Architecture:** MVVM (Model-View-ViewModel)
- **Database:** SQLite (Room Persistence Library)
- **UI:** XML + Material Design Components

---

## 3. System Architecture
The application follows a layered architecture:

- **UI Layer:** Activities/Fragments (screens)
- **ViewModel Layer:** Handles UI logic
- **Repository Layer:** Manages data operations
- **Data Layer:** Room Database (Entities + DAO)

---

## 4. Database Design

### Tables:
- **Users**
    - id, name, email, password, interests, goal, experience, careerStage, hobbies, xp, subscription

- **Courses**
    - id, title, description, category, level, imageUrl

- **Enrollments**
    - id, userId, courseId, progress

- **Subscriptions**
    - id, userId, plan, startDate

---

## 5. Core Modules

### 5.1 Authentication Module [DONE]
- User registration and login
- Local credential validation using SQLite
- Session management via SharedPreferences

---

### 5.2 Onboarding Module [DONE]
- 6-step personalized survey
- Data persisted to User entity upon completion
- Navigation flow from registration to main app

---

### 5.3 Course Management Module [DONE]
- Display course list via RecyclerView
- View course details with enrollment status
- **Search functionality** to find courses by title
- **Category filtering** (IT, Health, Teaching, etc.)

---

### 5.4 Enrollment & Progress Module [DONE]
- "Enroll Now" logic creating database entries
- Real-time enrollment status checking
- Dashboard displaying active enrollments and progress bars

---

### 5.5 User Profile Management [DONE]
- Display user data from Room
- **Edit Profile** functionality (Update name and email)
- Logout and session clearing

---

### 5.6 Subscription Module [DONE]
- Free and Premium plans
- UI screens completed
- Logic for plan restrictions (implemented for Course Details)

---

### 5.7 Gamification [IN PROGRESS]
- XP (experience points) system
- Reward tracking on Dashboard
- Automatic XP rewards for milestones (Planned)

---

## 6. User Interface Structure

### Authentication [DONE]
- Login Screen
- Register Screen
- Auth Landing Screen

### Onboarding [DONE]
- Goal Selection
- Experience Selection
- Interests (2 Steps)
- Career Stage
- Hobbies

### Main Navigation [DONE]
- Home
- Explore (with Search/Filters)
- Learn (Dashboard)
- Profile (with Edit functionality)

---

## 7. Development Phases

### Phase 1 [DONE]
- Project setup
- Room database configuration

### Phase 2 [DONE]
- Authentication and onboarding integration

### Phase 3 [DONE]
- Course listing, search, and filtering

### Phase 4 [DONE]
- Enrollment logic and Dashboard progress

### Phase 5 [DONE]
- Profile management and editing

### Phase 6 [DONE]
- Subscription logic and gamification basics

### Phase 7 [TODO]
- UI refinement and final testing

---

## 8. Limitations
- Offline-only system
- Data stored locally on device
- No cloud-based authentication

---

## 9. Future Improvements
- Integrate Firebase for cloud sync
- Add real payment gateway
- Advance recommendation algorithms

---

## 10. Conclusion
The LearnBridge application has successfully transitioned from a UI prototype to a functional offline-first app. Core features including authentication, onboarding, course enrollment, and profile management are fully integrated with the Room database.
