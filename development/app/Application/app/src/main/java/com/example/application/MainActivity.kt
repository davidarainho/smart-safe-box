package com.example.application

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.application.data.LockDBSingleton
import com.example.application.data.UserAndLock.UserAndLockDao
import com.example.application.data.UserAndLockDBSingleton
import com.example.application.data.UserDBSingleton
import com.example.application.data.lock.LockDao
import com.example.application.data.user.UserDao
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        LockDBSingleton.getInstance(applicationContext);
        UserDBSingleton.getInstance(applicationContext);
        UserAndLockDBSingleton.getInstance(applicationContext);

        GlobalScope.launch {
            deleteData()
        }
    }

    private suspend fun deleteData() = runBlocking {
        val lockDatabase = LockDBSingleton.getInstance(applicationContext)
        val lockDao: LockDao? = lockDatabase!!.getAppDatabase().lockDao()

        val userDatabase = UserDBSingleton.getInstance(applicationContext)
        val userDao: UserDao = userDatabase!!.getAppDatabase().userDao()

        val userAndLockDatabase = UserAndLockDBSingleton.getInstance(applicationContext)
        val userLockDao: UserAndLockDao? = userAndLockDatabase!!.getAppDatabase().userAndLockDao()

        withContext(Dispatchers.IO) {
            lockDao?.deleteLockData()
            userDao.deleteUserData()
            userLockDao?.deleteUserLockData()
        }
    }
}