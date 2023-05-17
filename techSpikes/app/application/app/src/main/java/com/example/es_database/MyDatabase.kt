package com.example.es_database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.es_database.lock.Lock
import com.example.es_database.lock.LockDao
import com.example.es_database.user.User
import com.example.es_database.user.UserDao
import com.example.es_database.userlock.UserLock
import com.example.es_database.userlock.UserLockDao
/*
@Database(entities = [User::class, Lock::class, UserLock::class], version = 1)
abstract class MyDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun lockDao(): LockDao
    abstract fun userLockDao(): UserLockDao

    companion object {
        @Volatile
        private var instance: MyDatabase? = null

        fun getInstance(context: Context): MyDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): MyDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                MyDatabase::class.java, "my-database"
            ).build()
        }
    }
}
*/