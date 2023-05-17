package com.example.es_database.lock

import androidx.room.*

@Dao
interface LockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLock(lock: Lock)

    @Delete
    suspend fun deleteLock(lock: Lock)

    //@Query("SELECT * FROM Lock ORDER BY lock_name ASC")
    //fun getLocksByName(): kotlinx.coroutines.flow.Flow<List<Lock>>

    // @Query("SELECT * FROM Lock ORDER BY last_access DESC")
    // fun getAllByLastAccess(): kotlinx.coroutines.flow.Flow<List<Lock>>

    @Query("SELECT * FROM Lock ORDER BY number_of_users DESC")
    fun orderLocksByUserNumber(): kotlinx.coroutines.flow.Flow<List<Lock>>

    @Query("SELECT * FROM Lock ORDER BY last_access DESC")
    fun orderLocksByLastAccess(): kotlinx.coroutines.flow.Flow<List<Lock>>

    //@Query("SELECT Lock.lock_name, Lock.user_last_access, Lock.last_access FROM Lock ORDER BY last_access DESC")
    //fun gelLockLastAccessInfo(): kotlinx.coroutines.flow.Flow<List<Lock>>

    @Query("SELECT lock_id FROM Lock LIMIT 1")
    suspend fun getFirstLockId(): Int

    @Query("SELECT * FROM Lock WHERE lock_id = :lockId")
    fun getLockByLockId(lockId: Int): kotlinx.coroutines.flow.Flow<Lock>

    @Query("SELECT * FROM Lock WHERE lock_name = :lockName")
    fun getLockByLockName(lockName: String): kotlinx.coroutines.flow.Flow<Lock>

    @Query("UPDATE Lock SET lock_name = :newLockName WHERE lock_id = :lockId")
    suspend fun updateLockName(lockId: Int, newLockName: String)

    @Query("SELECT lock_id FROM Lock WHERE lock_name = :lockName")
    suspend fun getLockIdByName(lockName: String): Int




}