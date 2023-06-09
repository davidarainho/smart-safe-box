package com.example.application.data.UserAndLock

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase

@Database(entities = [UserAndLock::class], version = 1)

abstract class UserAndLockDatabase : RoomDatabase() {

    abstract fun userAndLockDao(): UserAndLockDao?

    companion object {
        private var instance: UserAndLockDatabase? = null
        @Synchronized
        fun getLockDatabase(context: Context): UserAndLockDatabase? {
            if (instance == null) {
                instance = databaseBuilder(
                    context.applicationContext,
                    UserAndLockDatabase::class.java,
                    "user_and_lock_database"
                )
                    .build()
            }
            return instance
        }
    }
}