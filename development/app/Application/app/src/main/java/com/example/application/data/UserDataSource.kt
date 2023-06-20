package com.example.application.data

import android.content.Context
import com.example.application.data.UserAndLock.UserAndLockDao
import com.example.application.data.lock.LockDao
import com.example.application.data.user.User
import com.example.application.data.user.UserDao
import com.example.myapplication.functions.serverConnectionFunctions
import com.example.myapplication.model.LockConn
import com.example.myapplication.model.UserConn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.streams.toList

class UserDataSource (val username : String) {

    private val functionConnection = serverConnectionFunctions()


    fun loadUserInfo(context: Context, lockID : Int): List<String>? = runBlocking {
        var lock: LockConn? = null
        withContext(Dispatchers.IO) {
             lock = functionConnection.getLockConnLogin(username, lockID.toString())
        }
        lock?.users_with_access
    }

}