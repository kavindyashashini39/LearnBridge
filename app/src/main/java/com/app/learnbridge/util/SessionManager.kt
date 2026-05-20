package com.app.learnbridge.util

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("LearnBridgePrefs", Context.MODE_PRIVATE)

    companion object {
        const val USER_ID = "user_id"
        const val IS_LOGGED_IN = "is_logged_in"
    }

    fun saveUserId(userId: Int) {
        prefs.edit().putInt(USER_ID, userId).apply()
        prefs.edit().putBoolean(IS_LOGGED_IN, true).apply()
    }

    fun getUserId(): Int {
        return prefs.getInt(USER_ID, -1)
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(IS_LOGGED_IN, false)
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}
