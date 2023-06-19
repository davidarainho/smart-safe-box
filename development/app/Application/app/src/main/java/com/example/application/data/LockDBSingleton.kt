package com.example.application.data

import android.content.Context
import androidx.room.Room.databaseBuilder
import com.example.application.data.lock.LockDao
import com.example.application.data.lock.LockDatabase


class LockDBSingleton private constructor(context: Context) {
    private val lockDatabase: LockDatabase

    init {
        lockDatabase = databaseBuilder(
            context.getApplicationContext(),
            LockDatabase::class.java, "new_lock_database"
        ).build()
    }

    fun getAppDatabase(): LockDatabase {
        return lockDatabase
    }

    companion object {
        private var instance: LockDBSingleton? = null
        @Synchronized
        fun getInstance(context: Context): LockDBSingleton? {
            if (instance == null) {
                instance = LockDBSingleton(context)
            }
            return instance
        }
    }
}
