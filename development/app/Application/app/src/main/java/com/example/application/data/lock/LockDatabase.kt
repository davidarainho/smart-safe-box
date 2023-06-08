package com.example.application.data.lock

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database
    (
    entities = [Lock::class],
    version = 1
)

abstract class LockDatabase: RoomDatabase() {

    abstract fun lockDao(): LockDao

    companion object {
        @Volatile
        private var instance: LockDatabase? = null

        fun getLockDatabase(context: Context): LockDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    LockDatabase::class.java,
                    "lock_database"
                ).build()
                instance = newInstance
                newInstance
            }
        }
    }


}