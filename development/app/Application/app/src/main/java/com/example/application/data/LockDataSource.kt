package com.example.application.data

import android.content.Context
import com.example.application.data.lock.Lock
import com.example.application.data.lock.LockDao
import kotlinx.coroutines.*

/*
class LockDataSource {
    //private lateinit var userLockDao: UserLockDao
    //private lateinit var lockDao: LockDao
    fun loadUserlockers(context : Context): List<Lock>? {
        // Passar o contexto?!

        val lockDatabaseSingleton = AppDatabaseSingleton.getInstance(context)
        val lockDao: LockDao? = lockDatabaseSingleton!!.getAppDatabase().lockDao()

// Use yourDao for database operations in your fragment
//
//
//        val lockDatabase = DBManager.getLockDatabase()
//        lockDao = lockDatabase.lockDao()!!

        var listOfLock : List<Lock>? = null
        GlobalScope.launch {
            // [REVER]
            //listOfLock = userLockDao.getLocksByUserId(2)
            if (lockDao != null) {
                listOfLock = lockDao.getAll()
            }
        }
        println(listOfLock)

        return listOfLock
    }
}*/

class LockDataSource {
    fun loadUserlockers(context: Context): List<Lock>? = runBlocking {
        val lockDatabaseSingleton = LockDBSingleton.getInstance(context)
        val lockDao: LockDao? = lockDatabaseSingleton!!.getAppDatabase().lockDao()

        var listOfLock: List<Lock>? = null
        withContext(Dispatchers.IO) {
            listOfLock = lockDao?.orderLocksByLastAccess()
        }
        println(listOfLock)

        listOfLock
    }
}