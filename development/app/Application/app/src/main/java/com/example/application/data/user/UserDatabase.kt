package com.example.application.data.user

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/*
@Database
    (
    entities = [User::class],
    version = 1
)

abstract class UserDatabase: RoomDatabase() {

    abstract fun userDao(): UserDao

}*/


@Database(entities = [User::class], version = 2)
abstract class UserDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
/*
    companion object {
        @Volatile
        private var instance: UserDatabase? = null

        fun getDatabase(context: Context): UserDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_database"
                ).build()
                instance = newInstance
                newInstance
            }
        }
    }*/

    companion object {
        @Volatile
        private var instance: UserDatabase? = null

        fun getDatabase(context: Context): UserDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                instance = newInstance
                newInstance
            }
        }
    }

}


val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
    }
}

