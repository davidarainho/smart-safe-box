package com.example.application.data.userLock

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.application.data.lock.Lock
import com.example.application.data.lock.LockDao
import com.example.application.data.user.User
import com.example.application.data.user.UserDao

@Database
    (
    entities = [UserLock::class, Lock::class, User::class],
    version = 1
)
abstract class UserLockDatabase : RoomDatabase() {

    abstract fun userLockDao(): UserLockDao

    abstract fun lockDao(): LockDao
    abstract fun userDao(): UserDao


}