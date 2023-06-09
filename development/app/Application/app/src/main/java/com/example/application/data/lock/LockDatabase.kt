package com.example.application.data.lock

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase

@Database(entities = [Lock::class], version = 2)
abstract class LockDatabase : RoomDatabase() {
    abstract fun lockDao(): LockDao?

    companion object {
        private var instance: LockDatabase? = null
        @Synchronized
        fun getLockDatabase(context: Context): LockDatabase? {
            if (instance == null) {
                instance = databaseBuilder(
                    context.applicationContext,
                    LockDatabase::class.java,
                    "lock_database"
                )
                    .fallbackToDestructiveMigration() // Use this to handle migrations
                    .build()
            }
            return instance
        }
    }
}