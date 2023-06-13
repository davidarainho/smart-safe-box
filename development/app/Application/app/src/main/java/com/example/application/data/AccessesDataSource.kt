package com.example.application.data

import android.content.Context
import com.example.application.data.UserAndLock.UserAndLockDao
import com.example.application.data.lock.Lock
import com.example.application.data.lock.LockDao
import com.example.application.data.user.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.streams.toList

class AccessesDataSource {
    fun loadAccesseslockers(context: Context, lockID : Int): Pair<List<String>?, List<String>?>  = runBlocking {
        val lockDatabaseSingleton = LockDBSingleton.getInstance(context)
        val lockDao : LockDao? = lockDatabaseSingleton!!.getAppDatabase().lockDao()

        var lock : Lock
        var listUserAccessed: List<String>? = null
        var listDateAccessed: List<String>? = null
        withContext(Dispatchers.IO) {
            if (lockDao != null) {
                lock = lockDao.getLockByLockId(lockID)
                listUserAccessed = lock.user_last_access.split(",")
                listDateAccessed = lock.last_access.split(",")
            }
        }



        Pair(listUserAccessed , listDateAccessed)
    }
}