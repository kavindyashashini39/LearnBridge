package com.app.learnbridge.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.app.learnbridge.repository.UserRepository
import kotlinx.coroutines.launch

class SurveyViewModel(private val repository: UserRepository) : ViewModel() {
    var goal: String? = null
    var experience: String? = null
    var interestArea: String? = null
    var careerStage: String? = null
    var hobbies: String? = null

    fun saveSurveyResults(userId: Int, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.getUserByIdOnce(userId)?.let { user ->
                val updatedUser = user.copy(
                    goal = goal,
                    experience = experience,
                    careerStage = careerStage,
                    hobbies = hobbies,
                    interests = interestArea,
                    surveyCompleted = true  // Mark survey as completed for this user
                )
                repository.updateUser(updatedUser)
                onComplete()
            }
        }
    }

    class SurveyViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SurveyViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SurveyViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
