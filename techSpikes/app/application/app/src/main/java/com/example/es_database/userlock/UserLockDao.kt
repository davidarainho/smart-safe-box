package com.example.es_database.userlock

import androidx.room.*
import com.example.es_database.lock.Lock
import com.example.es_database.user.User

@Dao
interface UserLockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUserLock(userlock: UserLock): Long

    @Delete
    suspend fun deleteUserLock(userlock: UserLock)

    @Transaction
    @Query("SELECT * FROM UserLock")
    fun getAllUserLocksWithRelations(): List<UserLock>

    @Transaction
    @Query("INSERT INTO UserLock (user_id_fk, lock_id_fk, userLockId, lock_access_pin) VALUES (:userId, :lockId, :userLockId, :lockAccessPin)")
    suspend fun createUserLockAssociation(userId: Int, lockId: Int, userLockId: Int, lockAccessPin: String)

    @Query("SELECT Lock.* FROM Lock JOIN UserLock ON Lock.lock_id = UserLock.lock_id_fk WHERE UserLock.user_id_fk = :userId")
    suspend fun getLocksByUserId(userId: Int): List<Lock>

    @Query("SELECT User.* FROM User JOIN UserLock ON User.user_id = UserLock.user_id_fk WHERE UserLock.lock_id_fk = :lockId")
    suspend fun getUsersByLockId(lockId: Int): List<User>

    @Query("UPDATE UserLock SET lock_access_pin = :newLockPin WHERE user_id_fk = :userId AND lock_id_fk = :lockId")
    suspend fun updateLockPin(userId: Int, lockId: Int, newLockPin: String)





}