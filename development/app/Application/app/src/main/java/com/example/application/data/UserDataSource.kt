package com.example.application.data

import android.content.Context
import com.example.application.data.user.User
import com.example.application.data.user.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class UserDataSource {
    fun loadUserInfo(context: Context): List<User>? = runBlocking {
        val userDatabaseSingleton = UserDBSingleton.getInstance(context)
        val userDao: UserDao? = userDatabaseSingleton!!.getAppDatabase().userDao()

        var listOfLock: List<User>?
        withContext(Dispatchers.IO) {
            listOfLock = userDao?.getUserByUsername() //orderLocksByLastAccess
        }
        println(listOfLock)

        listOfLock
    }
}