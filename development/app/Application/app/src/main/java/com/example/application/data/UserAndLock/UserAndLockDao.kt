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

    // returns list with lock id's
    @Query("SELECT lock_id FROM UserAndLock WHERE user_id = :userId")
    suspend fun getLocksIDByUserId(userId: Int): List<Int>


// exemplo para usar esta funçao:

//    val userId = 1
//    val lockIdsList = getLocksIDByUserId(userId)
//
//    for (lockId in lockIdsList) {
//        val lock = getLockByLockId(lockId)
//    }


    // returns list with user id's
    @Query("SELECT user_id FROM UserAndLock WHERE lock_id = :lockId")
    suspend fun getUsersIDByLockId(lockId: Int): List<Int>

    @Query("UPDATE UserAndLock SET lock_access_pin = :newLockPin WHERE user_id = :userId AND lock_id = :lockId")
    suspend fun updateLockPin(userId: Int, lockId: Int, newLockPin: String)

    @Query("SELECT permission_level FROM UserAndLock WHERE user_id = :userId AND lock_id = :lockId")
    suspend fun getUserLockPermissionLevel(userId: Int, lockId: Int): Int

    @Query("UPDATE UserAndLock SET permission_level = :newPermissionLevel WHERE user_id = :userId AND lock_id = :lockId")
    suspend fun updatePermissionLevel(userId: Int, lockId: Int, newPermissionLevel: Int)


    // retrieves the permission levels for all users associated with a specific lock
    @Query("SELECT user_id, permission_level FROM UserAndLock WHERE lock_id = :lockId")
    suspend fun getPermissionLevelsByLockId(lockId: Int): Map<Int, Int> {
        val userIds = getUsersIDByLockId(lockId)
        val permissionLevels = mutableMapOf<Int, Int>()

        for (userId in userIds) {
            val permissionLevel = getUserLockPermissionLevel(userId, lockId)
            permissionLevels[userId] = permissionLevel
        }

        return permissionLevels
    }


    // retrieves the permission levels for all lock associated with a specific user
    @Query("SELECT lock_id, permission_level FROM UserAndLock WHERE user_id = :userId")
    suspend fun getPermissionLevelsByUserId(userId: Int): Map<Int, Int> {
        val lockIds = getLocksIDByUserId(userId)
        val permissionLevels = mutableMapOf<Int, Int>()

        for (lockId in lockIds) {
            val permissionLevel = getUserLockPermissionLevel(userId, lockId)
            permissionLevels[lockId] = permissionLevel
        }

        return permissionLevels
    }

    @Query("SELECT user_id FROM UserAndLock WHERE lock_id = :lockId AND permission_level = :permissionLevel")
    suspend fun getUsersIDByPermissionLevel(lockId: Int, permissionLevel: Int): List<Int>

    @Query("SELECT lock_id FROM UserAndLock WHERE user_id = :userId AND permission_level = :permissionLevel")
    suspend fun getLocksIDByPermissionLevel(userId: Int, permissionLevel: Int): List<Int>

    @Query("DELETE FROM UserAndLock")
    fun deleteUserLockData()




}