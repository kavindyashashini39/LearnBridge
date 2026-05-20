package com.app.learnbridge.repository

import com.app.learnbridge.db.User
import com.app.learnbridge.db.UserDao
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    suspend fun insertUser(user: User): Long {
        return userDao.insertUser(user)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user)
    }

    suspend fun login(email: String, password: String): User? {
        return userDao.login(email, password)
    }

    fun getUserById(userId: Int): Flow<User?> {
        return userDao.getUserById(userId)
    }

    suspend fun getUserByIdOnce(userId: Int): User? {
        return userDao.getUserByIdOnce(userId)
    }

    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }
}
