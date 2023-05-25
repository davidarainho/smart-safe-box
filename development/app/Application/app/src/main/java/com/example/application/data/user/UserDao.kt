package com.example.application.data.user

import androidx.room.*
import kotlin.collections.List as List

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("SELECT * FROM User ORDER BY username ASC")
    fun getUserByUsername(): kotlinx.coroutines.flow.Flow<List<User>>

    @Query("SELECT * FROM User ORDER BY email ASC")
    fun getUserByEmail():  kotlinx.coroutines.flow.Flow<List<User>>

    //@Query("SELECT user_id FROM User LIMIT 1")
    //suspend fun getFirstUserId(): Int

    @Query("SELECT user_id FROM User WHERE username = :username")
    suspend fun getUserIdByUsername(username: String): Int

    @Query("SELECT user_id FROM User WHERE email = :Email")
    suspend fun getUserIdByEmail(Email: String): Int

    @Query("SELECT allow_notifications FROM User WHERE user_id = :userId")
    suspend fun getNotificationPreferenceByUserId(userId: Int): Int

    @Query("UPDATE User SET allow_notifications = :newPreference WHERE user_id = :userId")
    suspend fun updateNotificationPreference(userId: Int, newPreference: Int)

    @Query("SELECT * FROM User WHERE user_id = :userId")
    fun getUserByUserId(userId: Int): kotlinx.coroutines.flow.Flow<User>

    @Query("UPDATE User SET username = :newUsername WHERE user_id = :userId")
    suspend fun updateUsername(userId: Int, newUsername: String)

    @Query("UPDATE User SET password = :newPassword WHERE user_id = :userId")
    suspend fun updatePassword(userId: Int, newPassword: String)

    @Query("UPDATE User SET email = :newEmail WHERE user_id = :userId")
    suspend fun updateEmail(userId: Int, newEmail: String)





}