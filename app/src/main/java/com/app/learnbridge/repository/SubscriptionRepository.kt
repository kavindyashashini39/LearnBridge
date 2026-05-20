package com.app.learnbridge.repository

import com.app.learnbridge.db.Subscription
import com.app.learnbridge.db.SubscriptionDao
import com.app.learnbridge.db.Transaction
import com.app.learnbridge.db.TransactionDao
import kotlinx.coroutines.flow.Flow

class SubscriptionRepository(
    private val subscriptionDao: SubscriptionDao,
    private val transactionDao: TransactionDao
) {

    fun getLatestSubscription(userId: Int): Flow<Subscription?> {
        return subscriptionDao.getLatestSubscription(userId)
    }

    suspend fun getLatestSubscriptionOnce(userId: Int): Subscription? {
        return subscriptionDao.getLatestSubscriptionOnce(userId)
    }

    suspend fun upgradeToPremium(userId: Int) {
        val subscription = Subscription(
            userId = userId,
            plan = "Premium",
            startDate = System.currentTimeMillis()
        )
        subscriptionDao.insertSubscription(subscription)

        // Record transaction
        val transaction = Transaction(
            userId = userId,
            planName = "Premium Upgrade",
            amount = 9.99, // Assuming a fixed price for prototype
            timestamp = System.currentTimeMillis()
        )
        transactionDao.insertTransaction(transaction)
    }

    suspend fun cancelSubscription(userId: Int) {
        subscriptionDao.cancelSubscription(userId)
    }
}
