package com.example.myapplication.api

import com.example.myapplication.model.LockConn
import com.example.myapplication.model.UserConn
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Field
import com.google.gson.annotations.SerializedName

interface SimpleApi {

    //testadas

    data class EmailResponse(val email: String)

    @GET("/user/email")
    suspend fun getEmail(@Query("username") username: String): Response<EmailResponse>


    @GET("/user")
    suspend fun getUser(@Query("username") username: String): Response<UserConn?>


    @POST("/createUser")
    suspend fun createUser(@Query("username") username:String, @Query("password") password:String, @Query("email") email:String, @Query("accessLevel0") accessLevel0: Int, @Query("accessPin") accessPin:String): Response<UserConn?>






    // a testar

    @GET("/user/login")
    suspend fun login(@Query("username") username: String, @Query("loginPassword") loginPassword: String): Response<UserConn?>



    /*
        @GET("/user/active_locks")
        suspend fun getActiveLocks(@Query("username") username: String): Response<List<ActiveLocks>>


     */
    @GET("/user/lock")
    suspend fun getLock(@Query("username") username: String, @Query("lockID") lockID: String): Response<LockConn>




    //não testadas















    @POST("/user/username")
    suspend fun updateUserUsername(@Query("oldUsername") oldUsername: String, @Query("newUsername") newUsername: String): Response<String?>

    @POST("/user/password")
    suspend fun updateUserPassword(@Query("username") username: String, @Query("oldPassword") oldPassword: String, @Query("newPassword") newPassword: String): Response<Any?>


    @POST("/user/updateUserPin")
    suspend fun updateUserPin(@Query("username") username: String, @Query("newPin") newPin:String, @Query("oldPin") oldPin: String): Response<Any?>








    @POST("/user/allocateLock")
    suspend fun allocateLock(@Query("username") username: String, @Query("lockID") lockID: String, @Query("accessLevel") accessLevel:Int): Response<Any?>



    @POST("/user/deallocateLock")
    suspend fun deallocateLock(@Query("username") username: String, @Query("lockID") lockID:String): Response<Any?>



    @POST("/lock/updateLockLocation")
    suspend fun updateLockLocation(@Query("lockID") lockID:String, @Query("newLocation") newLocation:String): Response<Any?>

    @POST("/lock/updateLockName")
    suspend fun updateLockName(@Query("lockID") lockID:String, @Query("newName") newName:String): Response<Any?>


    @POST("/firstInteraction")
    suspend fun firstInteraction(@Query("username") username:String, @Query("appcode") appcode: String): Response<Any?>

    @POST("/lock/openLocks")
    suspend fun openLocks(@Query("username") username:String): Response<Any?>






}