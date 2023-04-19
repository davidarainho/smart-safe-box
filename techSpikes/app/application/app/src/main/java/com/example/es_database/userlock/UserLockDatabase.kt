package com.example.es_database.userlock

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.es_database.lock.Lock
import com.example.es_database.user.User

@Database
    (
        entities = [UserLock::class, Lock::class, User::class],
        version = 1
)
abstract class UserLockDatabase : RoomDatabase() {

    abstract val dao: UserLockDao

}