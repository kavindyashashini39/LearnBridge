 LearnBridge 

LearnBridge is a native Android educational application engineered to streamline online learning, course progression tracking, and student profile management. Built using Kotlin and modern Material Design components, the platform delivers a seamless, responsive mobile experience designed to bridge the gap between students and structural educational content.

---

 Project Architecture & Team Roles

This project was collaboratively developed by our engineering team. Each member assumed ownership of a distinct core functional module:

| Student ID | Team Role | Module & Key Responsibilities |
| :--- | :--- | :--- |
| ITBNM-2313-0039 | Member 01 | User Profile Management<br>• Designed the onboarding quiz pipelines (`fragment_survey_interests`).<br>• Developed registration setups, login authentication modules, and user configuration states. |
| ITBNM-2313-0049 | Member 02 | Learning Content Management<br>• Implemented structural layouts for course browsing and material categorization.<br>• Managed dynamic asset integration, resource directories, and core database interactions. |
| ITBNM-2313-0007 | Member 03 | Enrollment & Learning Progress Management<br>• Programmed enrollment states, transaction logic, and user milestone analytics.<br>• Rendered custom progress indicators (`custom_progress_bar`) and real-time tracking graphs. |
| ITBNM-2313-0063 | Member 04 | Subscription & Payment Management<br>• Engineered subscription tiers, professional digital card modules (`bg_card_brand`), and transaction history panels.<br>• Simulated safe premium checkouts and digital wallet verification templates. |

---

 Key Features

Tailored User Onboarding:** Dynamic onboarding screens capture student learning preferences via intuitive multi-step surveys.
  Component-Driven UI/UX:** High-performance, modular graphical assets featuring premium badge rewards (`bg_pro_badge`), input forms, and dynamic action states (`bg_action_blue`).
  Analytical Dashboards:** Comprehensive view tracking that updates structural progress components instantly as students complete milestones.
  Secure Checkout Emulation:** Specialized checkout fields supporting credit card structures and explicit breakdown lists for transaction verifications.

---

 Technical Stack & Dependencies

Platform: Native Android Mobile Application
Development Language: Kotlin / Java
UI Engine: XML Layouts (Android Material Design Components)
Build Configuration: Gradle (Kotlin DSL / Groovy)
Minimum SDK Support: API 24 (Android 7.0 Nougat)
Target SDK Version: API 34/35

---

 Repository Directory Insights

The source structure adheres strictly to standard clean Android architecture guidelines:
`app/src/main/res/layout/`: Contains core structural fragments for onboarding interest selectors, subscription screens, profile settings, and payment summaries.
  `app/src/main/res/drawable/`: Houses optimized resource vectors including brand assets (`google.png`, `ibm.png`, `microsoft.png`) and reusable layout backgrounds.

---

 Local Development Setup

To replicate, audit, or run this project locally, execute the following steps:

1. Clone the Repository:
   ```bash
   git clone [https://github.com/YOUR_GITHUB_USERNAME/LearnBridge.git](https://github.com/YOUR_GITHUB_USERNAME/LearnBridge.git)
