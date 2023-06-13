package com.example.application.data

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.application.data.UserAndLock.UserAndLockDao
import com.example.application.data.lock.Lock
import com.example.application.data.lock.LockDao
import com.example.application.data.user.UserDao
import kotlinx.coroutines.*
import kotlin.streams.toList

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

    fun loadUserlockers(context: Context, username : String): List<Lock>? = runBlocking {
        val lockDatabaseSingleton = LockDBSingleton.getInstance(context)
        val lockDao : LockDao? = lockDatabaseSingleton!!.getAppDatabase().lockDao()

        val userDatabaseSingleton = UserDBSingleton.getInstance(context)
        val userDao : UserDao = userDatabaseSingleton!!.getAppDatabase().userDao()

        val userAndLockDatabase = UserAndLockDBSingleton.getInstance(context)
        val userLockDao : UserAndLockDao? = userAndLockDatabase!!.getAppDatabase().userAndLockDao()

        var userId : Int
        var lockID : List<Int>
        var listOfLock: List<Lock>?
        withContext(Dispatchers.IO) {
//            println("1")
//            println("2")
//            userId = userDao.getUserIdByUsername(username)
//            if (userLockDao != null) {
//                println("3")
//                lockID = userLockDao.getLocksIDByUserId(userId)
//
//                if (lockDao != null) {
//                    println("4")
//                    listOfLock = lockID.stream().map{l -> lockDao.getLockByLockId(l)}.toList()
//                }
//
//            }
            println(username)
            listOfLock = lockDao?.getAll()
        }
        //println(listOfLock)

        listOfLock
    }
}