package com.example.es_database.lock

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface LockDao {

    @Upsert
    suspend fun upsertLock(lock: Lock)

    @Delete
    suspend fun deleteLock(lock: Lock)

    @Query("SELECT * FROM Lock ORDER BY lock_name ASC")
    fun getLocksByName(): kotlinx.coroutines.flow.Flow<List<Lock>>

    // @Query("SELECT * FROM Lock ORDER BY last_access DESC")
    // fun getAllByLastAccess(): kotlinx.coroutines.flow.Flow<List<Lock>>

    @Query("SELECT * FROM Lock ORDER BY number_of_users DESC")
    fun getAllByNUsers(): kotlinx.coroutines.flow.Flow<List<Lock>>

}