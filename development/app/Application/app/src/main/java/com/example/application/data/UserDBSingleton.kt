package com.example.application.data
import android.content.Context
import androidx.room.Room.databaseBuilder
import com.example.application.data.user.UserDatabase

class UserDBSingleton private constructor(context: Context) {
    private val userDatabase: UserDatabase

    init {
        userDatabase = databaseBuilder(
            context.getApplicationContext(),
            UserDatabase::class.java, "new_user_database"
        ).build()
    }

    fun getAppDatabase(): UserDatabase {
        return userDatabase
    }

    companion object {
        private var instance: UserDBSingleton? = null
        @Synchronized
        fun getInstance(context: Context): UserDBSingleton? {
            if (instance == null) {
                instance = UserDBSingleton(context)
            }
            return instance
        }
    }
}
