package com.example.es_database.user
import androidx.room.*
import kotlin.collections.List as List

@Dao
interface UserDao {

    @Upsert
    suspend fun upsertUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("SELECT * FROM User ORDER BY username ASC")
    fun getUserByUsername(): kotlinx.coroutines.flow.Flow<List<User>>

    @Query("SELECT * FROM User ORDER BY email ASC")
    fun getUserByEmail():  kotlinx.coroutines.flow.Flow<List<User>>

}
