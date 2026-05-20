package com.app.learnbridge.repository

import com.app.learnbridge.db.Transaction
import com.app.learnbridge.db.TransactionDao
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {
    fun getTransactionsByUser(userId: Int): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByUser(userId)
    }

    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }
}
