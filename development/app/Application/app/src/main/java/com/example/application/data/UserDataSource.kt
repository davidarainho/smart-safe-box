package com.example.application.data

import android.content.Context
import com.example.application.data.UserAndLock.UserAndLockDao
import com.example.application.data.lock.LockDao
import com.example.application.data.user.User
import com.example.application.data.user.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.streams.toList

class UserDataSource {
    fun loadUserInfo(context: Context, lockID : Int): List<User>? = runBlocking {

        val userDatabaseSingleton = UserDBSingleton.getInstance(context)
        val userDao : UserDao = userDatabaseSingleton!!.getAppDatabase().userDao()

        val userAndLockDatabase = UserAndLockDBSingleton.getInstance(context)
        val userLockDao : UserAndLockDao? = userAndLockDatabase!!.getAppDatabase().userAndLockDao()

        var listOfUser: List<User>? = null
        var usersID : List<Int>
        withContext(Dispatchers.IO) {
            if (userLockDao != null) {
                usersID = userLockDao.getUsersIDByLockId(lockID)
                listOfUser = usersID.stream().map { l -> userDao.getUserByUserId(l) }.toList()
            }

        }

        listOfUser
    }
}