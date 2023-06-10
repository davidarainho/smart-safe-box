package com.example.application.data.lock

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLock(lock: Lock)

    @Delete
    suspend fun deleteLock(lock: Lock)

    @Query("SELECT * FROM Lock ORDER BY lock_name ASC")
    fun orderLocksByName(): Flow<List<Lock>>

//    @Query("SELECT lock_name, user_last_access, last_access FROM Lock ORDER BY last_access DESC")
//    fun getLockLastAccessInfo(): kotlinx.coroutines.flow.Flow<List<Lock>>


    @Query("SELECT * FROM Lock ORDER BY number_of_users DESC")
    fun orderLocksByUserNumber(): Flow<List<Lock>>

    @Query("DELETE FROM Lock")
    fun deleteLockData()

    @Query("SELECT * FROM Lock ORDER BY last_access DESC")
    fun orderLocksByLastAccess(): List<Lock>

    @Query("SELECT * FROM Lock")
    fun getAll(): List<Lock>

    @Query("SELECT lock_id FROM Lock LIMIT 1")
    suspend fun getFirstLockId(): Int

    @Query("SELECT lock_id FROM Lock ORDER BY lock_id DESC LIMIT 1")
    suspend fun getLastLockId(): Int

    @Query("SELECT * FROM Lock WHERE lock_id = :lockId")
    fun getLockByLockId(lockId: Int): Lock

    @Query("SELECT * FROM Lock WHERE lock_name = :lockName")
    fun getLockByLockName(lockName: String): Lock

    @Query("UPDATE Lock SET lock_name = :newLockName WHERE lock_id = :lockId")
    suspend fun updateLockName(lockId: Int, newLockName: String)

    @Query("SELECT lock_id FROM Lock WHERE lock_name = :lockName")
    suspend fun getLockIdByName(lockName: String): Int

    @Query("UPDATE Lock SET comment = :newComment WHERE lock_id = :lockId")
    suspend fun updateLockComment(lockId: Int, newComment: String)

    @Query("SELECT comment FROM Lock WHERE lock_id = :lockId")
    suspend fun getLockComment(lockId: Int): String

    @Query("SELECT eKey FROM Lock")
    suspend fun getLockEKey(): String

    @Query("UPDATE Lock SET eKey = :newKey WHERE lock_id = :lockId")
    suspend fun updateLockEKey(lockId: Int, newKey: String)

    @Query("SELECT lock_state FROM Lock")
    suspend fun getLockState(): String

    @Query("UPDATE Lock SET lock_state = :newState WHERE lock_id = :lockId")
    suspend fun updateLockState(lockId: Int, newState: String)

    @Query("SELECT last_access FROM Lock")
    suspend fun getLockLastAccessDates(): String

    @Query("SELECT user_last_access FROM Lock")
    suspend fun getLockLastAccessUsers(): String

    // não dá para guardar listas em bases de dados ent para o user_last_access e last_access
    // temos que receber tudo num unica string separada por virgulas e depois fazer parse
    // com esta função:
    suspend fun parseLockLastAccessDates(lockLastAccess: String): List<String> {
        return lockLastAccess.split(",")
    }

    suspend fun parseLockLastAccessUser(lockLastAccessUser: String): List<String> {
        return lockLastAccessUser.split(",")
    }

    // e usar assim por exemplo:
    // val lockLastAccessDateString = getLockLastAccessDates()
    // val lockLastAccessList = parseLockLastAccessDates(lockLastAccessDateString)













}