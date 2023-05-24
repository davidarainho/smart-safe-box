package com.example.application.data.userLock

import androidx.room.*
import com.example.application.data.lock.Lock
import com.example.application.data.user.User


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

    @Query("SELECT permission_level FROM UserLock WHERE user_id_fk = :userId AND lock_id_fk = :lockId")
    suspend fun getUserLockPermissionLevel(userId: Int, lockId: Int): Int

    @Query("UPDATE UserLock SET permission_level = :newPermissionLevel WHERE user_id_fk = :userId AND lock_id_fk = :lockId")
    suspend fun updatePermissionLevel(userId: Int, lockId: Int, newPermissionLevel: Int)

    // Retrieves the permission levels for all users associated with a specific lock
    @Query("SELECT user_id_fk, permission_level FROM UserLock WHERE lock_id_fk = :lockId")
    suspend fun getPermissionLevelsByLockId(lockId: Int): Map<Int, Int> {
        val users = getUsersByLockId(lockId)
        val permissionLevels = mutableMapOf<Int, Int>()

        for (user in users) {
            val permissionLevel = getUserLockPermissionLevel(user.user_id, lockId)
            permissionLevels[user.user_id] = permissionLevel
        }

        return permissionLevels
    }


    // retrieves the permission levels for all lock associated with a specific user
    @Query("SELECT lock_id_fk, permission_level FROM UserLock WHERE user_id_fk = :userId")
    suspend fun getPermissionLevelsByUserId(userId: Int): Map<Int, Int> {
        val locks = getLocksByUserId(userId)
        val permissionLevels = mutableMapOf<Int, Int>()

        for (lock in locks) {
            val permissionLevel = getUserLockPermissionLevel(userId, lock.lock_id)
            if (permissionLevel != null) {
                permissionLevels[lock.lock_id] = permissionLevel
            }
        }

        return permissionLevels
    }

    @Query("SELECT * FROM User WHERE user_id IN (SELECT user_id_fk FROM UserLock WHERE lock_id_fk = :lockId AND permission_level = :permissionLevel)")
    suspend fun getUsersByPermissionLevel(lockId: Int, permissionLevel: Int): List<User>

    @Query("SELECT * FROM Lock WHERE lock_id IN (SELECT lock_id_fk FROM UserLock WHERE user_id_fk = :userId AND permission_level = :permissionLevel)")
    suspend fun getLocksByPermissionLevel(userId: Int, permissionLevel: Int): List<Lock>











}