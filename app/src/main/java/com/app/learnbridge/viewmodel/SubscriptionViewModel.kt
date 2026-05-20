package com.app.learnbridge.viewmodel

import androidx.lifecycle.*
import com.app.learnbridge.db.Subscription
import com.app.learnbridge.repository.SubscriptionRepository
import kotlinx.coroutines.launch

class SubscriptionViewModel(private val repository: SubscriptionRepository) : ViewModel() {

    fun getLatestSubscription(userId: Int): LiveData<Subscription?> {
        return repository.getLatestSubscription(userId).asLiveData()
    }

    fun upgradeToPremium(userId: Int) {
        viewModelScope.launch {
            repository.upgradeToPremium(userId)
        }
    }

    fun cancelSubscription(userId: Int) {
        viewModelScope.launch {
            repository.cancelSubscription(userId)
        }
    }

    class SubscriptionViewModelFactory(private val repository: SubscriptionRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SubscriptionViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SubscriptionViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
