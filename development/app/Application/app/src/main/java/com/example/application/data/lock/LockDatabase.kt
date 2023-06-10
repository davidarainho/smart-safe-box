package com.example.application.data.lock

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

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
                    //.addMigrations(Migration1to2())
                    .fallbackToDestructiveMigration() // Use this to handle migrations
                    .build()
            }
            return instance
        }
    }
}

class Migration1to2 : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create a new table with the desired schema
        database.execSQL("CREATE TABLE Lock_new (lock_name TEXT, last_access TEXT, user_last_access TEXT, number_of_users INTEGER, comment TEXT, eKey TEXT, lock_state TEXT, lock_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")

        // Copy data from the existing table to the new table
        database.execSQL("INSERT INTO Lock_new (lock_name, last_access, user_last_access, number_of_users, comment, eKey, lock_state, lock_id) SELECT lock_name, last_access, user_last_access, number_of_users, comment, eKey, lock_state, lock_id FROM Lock")

        // Drop the old table
        database.execSQL("DROP TABLE Lock")

        // Rename the new table to the original table name
        database.execSQL("ALTER TABLE Lock_new RENAME TO Lock")
    }
}
