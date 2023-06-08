package com.example.application.data.userLock

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.application.data.lock.Lock
import com.example.application.data.lock.LockDao
import com.example.application.data.user.User
import com.example.application.data.user.UserDao

@Database
    (
    entities = [UserLock::class, Lock::class, User::class],
    version = 1
)
abstract class UserLockDatabase : RoomDatabase() {

    abstract fun userLockDao(): UserLockDao

    abstract fun lockDao(): LockDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var instance: UserLockDatabase? = null

        fun getUserLockDatabase(context: Context): UserLockDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    UserLockDatabase::class.java,
                    "user_lock_database"
                ).build()
                instance = newInstance
                newInstance
            }
        }
    }

}