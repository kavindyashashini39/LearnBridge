package com.app.learnbridge.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: Subscription)

    @Query("SELECT * FROM subscriptions WHERE userId = :userId ORDER BY startDate DESC LIMIT 1")
    fun getLatestSubscription(userId: Int): Flow<Subscription?>

    @Query("SELECT * FROM subscriptions WHERE userId = :userId ORDER BY startDate DESC LIMIT 1")
    suspend fun getLatestSubscriptionOnce(userId: Int): Subscription?

    @Query("DELETE FROM subscriptions WHERE userId = :userId")
    suspend fun cancelSubscription(userId: Int)
}
