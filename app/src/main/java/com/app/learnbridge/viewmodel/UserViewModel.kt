package com.app.learnbridge.viewmodel

import androidx.lifecycle.*
import com.app.learnbridge.db.User
import com.app.learnbridge.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _loginStatus = MutableLiveData<User?>(null)
    val loginStatus: LiveData<User?> = _loginStatus

    private val _registrationStatus = MutableLiveData<User?>(null)
    val registrationStatus: LiveData<User?> = _registrationStatus

    // Error states
    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    fun register(user: User) {
        viewModelScope.launch {
            _error.value = null
            val resultId = repository.insertUser(user)
            if (resultId > 0) {
                _registrationStatus.value = user.copy(id = resultId.toInt())
            } else {
                _error.value = "Registration failed"
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _error.value = null
            val user = repository.login(email, password)
            if (user != null) {
                _loginStatus.value = user
            } else {
                _error.value = "Invalid email or password"
            }
        }
    }

    fun getUserById(userId: Int): LiveData<User?> {
        return repository.getUserById(userId).asLiveData()
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            repository.updateUser(user)
        }
    }

    fun addXp(userId: Int, amount: Int) {
        viewModelScope.launch {
            val user = repository.getUserByIdOnce(userId)
            user?.let {
                val updatedUser = it.copy(xp = it.xp + amount)
                repository.updateUser(updatedUser)
            }
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            repository.deleteUser(user)
        }
    }

    class UserViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return UserViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
