package com.example.application.data.UserAndLock

import androidx.room.*
import com.example.application.data.lock.Lock
import com.example.application.data.user.User
import kotlin.collections.List as List

@Dao
interface UserAndLockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUserAndLock(userandlock: UserAndLock): Long

    @Delete
    suspend fun deleteUserAndLock(userandlock: UserAndLock)

    @Transaction
    @Query("SELECT * FROM UserAndLock")
    fun getAllUserAndLocksWithRelations(): List<UserAndLock>

    @Transaction
    @Query("INSERT INTO UserAndLock (user_id, lock_id, userLockId, lock_access_pin) VALUES (:userId, :lockId, :userLockId, :lockAccessPin)")
    suspend fun createUserLockAssociation(userId: Int, lockId: Int, userLockId: Int, lockAccessPin: String)

    @Query("SELECT lock_id FROM UserAndLock WHERE user_id = :userId")
    suspend fun getLocksByUserId(userId: Int): List<Int>

//    @Query("SELECT Lock.* FROM Lock JOIN UserAndLock ON Lock.lock_id = UserAndLock.lock_id WHERE UserAndLock.user_id = :userId")
//    suspend fun getLocksByUserId(userId: Int): List<Lock>

/*
    @Query("SELECT User.* FROM User JOIN UserAndLock ON User.user_id = UserAndLock.user_id WHERE UserAndLock.lock_id = :lockId")
    suspend fun getUsersByLockId(lockId: Int): List<User>

    @Query("UPDATE UserAndLock SET lock_access_pin = :newLockPin WHERE user_id = :userId AND lock_id = :lockId")
    suspend fun updateLockPin(userId: Int, lockId: Int, newLockPin: String)

    @Query("SELECT permission_level FROM UserAndLock WHERE user_id = :userId AND lock_id = :lockId")
    suspend fun getUserLockPermissionLevel(userId: Int, lockId: Int): Int

//    @Query("UPDATE UserAndLock SET permission_level = :newPermissionLevel WHERE user_id = :userId AND lock_id = :lockId")
//    suspend fun updatePermissionLevel(userId: Int, lockId: Int, newPermissionLevel: Int)


    // Retrieves the permission levels for all users associated with a specific lock
    @Query("SELECT user_id, permission_level FROM UserAndLock WHERE lock_id = :lockId")
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
    @Query("SELECT lock_id, permission_level FROM UserAndLock WHERE user_id = :userId")
    suspend fun getPermissionLevelsByUserId(userId: Int): Map<Int, Int> {
        val locks = getLocksByUserId(userId)
        val permissionLevels = mutableMapOf<Int, Int>()

        for (lock in locks) {
            val permissionLevel = getUserLockPermissionLevel(userId, lock.lock_id)
            permissionLevels[lock.lock_id] = permissionLevel
        }

        return permissionLevels
    }

    @Query("SELECT * FROM User WHERE user_id IN (SELECT user_id FROM UserAndLock WHERE lock_id = :lockId AND permission_level = :permissionLevel)")
    suspend fun getUsersByPermissionLevel(lockId: Int, permissionLevel: Int): List<User>

    @Query("SELECT * FROM Lock WHERE lock_id IN (SELECT lock_id FROM UserAndLock WHERE user_id = :userId AND permission_level = :permissionLevel)")
    suspend fun getLocksByPermissionLevel(userId: Int, permissionLevel: Int): List<Lock>*/



}