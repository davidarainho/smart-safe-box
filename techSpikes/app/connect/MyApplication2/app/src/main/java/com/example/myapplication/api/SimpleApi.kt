package com.example.myapplication.api

import com.example.myapplication.model.Lock
import com.example.myapplication.model.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Field

interface SimpleApi {

    // getting

    @GET("/user/login")
    suspend fun login(@Query("username") username: String, @Query("loginPassword") loginPassword: String): Response<Any?>

    data class EmailResponse(val email: String)



    @GET("/user/active_locks")
    suspend fun getActiveLocks(@Query("username") username: String): Response<List<Lock>>

    @GET("/user/lock")
    suspend fun getLock(@Query("username") username: String, @Query("lockID") lockID: String): Response<Lock>




    @GET("/user/email")
    suspend fun getEmail(@Query("username") username: String): Response<EmailResponse>


    @GET("/user")
    suspend fun getUser(@Query("username") username: String): Response<User?>


    // creating or updating


    data class CreateUserBody(
        val username: String,
        val password: String,
        val email: String,
        val accessLevl0: Int,
        val acessPin: String
    )




    @POST("/user")
    suspend fun createUser(@Query("username") username:String, @Query("password") password:String, @Query("email") email:String, @Query("access_level") access_level: Int, @Query("access_pin") access_pin:String): Response<User?>


    //suspend fun createUser(@Body user: CreateUserBody): Response<User?>



    @POST("/user/username")
    suspend fun updateUserUsername(@Query("oldUsername") oldUsername: String, @Query("newUsername") newUsername: String): Response<Unit>

    @POST("/user/password")
    suspend fun updateUserPassword(@Query("username") username: String, @Query("oldPassword") oldPassword: String, @Query("newPassword") newPassword: String): Response<Unit>

    @POST("/user/lock")
    suspend fun allocateLock(@Query("username") username: String, @Body lock: Lock): Response<Lock>

    @POST("/user/lock")
    suspend fun openUserLock(@Query("username") username: String, @Body lockID: String): Response<Unit>
/*
    @POST("/user/updateUserPin")
    suspend fun updateUserPin(@Query("username") username: String, @Body("newPin") newPin:String, @Query("oldPin") oldPin: String): Response<Unit>

    @POST("/user/deallocateLock")
    suspend fun deallocateLock(@Query("username") username: String, @Query("lockID") lockID:String): Response<Unit>

 */
}





