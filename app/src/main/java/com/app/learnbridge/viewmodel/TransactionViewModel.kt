package com.app.learnbridge.viewmodel

import androidx.lifecycle.*
import com.app.learnbridge.db.Transaction
import com.app.learnbridge.repository.TransactionRepository
import kotlinx.coroutines.launch

class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {

    fun getTransactionsByUser(userId: Int): LiveData<List<Transaction>> {
        return repository.getTransactionsByUser(userId).asLiveData()
    }

    class TransactionViewModelFactory(private val repository: TransactionRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TransactionViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
