package com.example.application.data
import android.content.Context
import androidx.room.Room.databaseBuilder
import com.example.application.data.UserAndLock.UserAndLockDatabase
import com.example.application.data.user.UserDatabase

class UserAndLockDBSingleton private constructor(context: Context) {
    private val userAndLockDatabase: UserAndLockDatabase

    init {
        userAndLockDatabase = databaseBuilder(
            context.getApplicationContext(),
            UserAndLockDatabase::class.java, "new_userANDlock_database"
        ).build()
    }

    fun getAppDatabase(): UserAndLockDatabase {
        return userAndLockDatabase
    }

    companion object {
        private var instance: UserAndLockDBSingleton? = null
        @Synchronized
        fun getInstance(context: Context): UserAndLockDBSingleton? {
            if (instance == null) {
                instance = UserAndLockDBSingleton(context)
            }
            return instance
        }
    }
}
