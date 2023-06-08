package com.example.application.data

import android.content.Context
import com.example.application.data.lock.Lock
import com.example.application.data.lock.LockDao
import com.example.application.data.lock.LockDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LockDataSource {
    //private lateinit var userLockDao: UserLockDao
    private lateinit var lockDao: LockDao
    fun loadUserlockers(context : Context): List<Lock>? {
        // Passar o contexto?!

        //userLockDao = UserLockDatabase.getUserLockDatabase(context).userLockDao()
        lockDao = LockDatabase.getLockDatabase(context).lockDao()

        var listOfLock : List<Lock>? = null
        GlobalScope.launch {
            // [REVER]
            //listOfLock = userLockDao.getLocksByUserId(2)
            listOfLock = lockDao.getAll()
        }
        println(listOfLock)

        return listOfLock
    }
}