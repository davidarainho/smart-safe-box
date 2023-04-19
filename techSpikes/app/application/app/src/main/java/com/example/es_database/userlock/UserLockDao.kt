package com.example.es_database.userlock

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Upsert

@Dao
interface UserLockDao {

    @Upsert
    suspend fun upsertUserLock(userlock: UserLock)

    @Delete
    suspend fun deleteUserLock(userlock: UserLock)

}