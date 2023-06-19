package com.example.myapplication.api

import com.example.myapplication.model.User
import com.example.myapplication.model.Door
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SimpleApi {

    @GET("/user/email")
    suspend fun checkEmail(@Query("email") email: String): Response<Any?>


    @GET("/user")
    suspend fun checkUsername(@Query("username") username: String): Response<User?>

    @GET("/door")
    suspend fun checkDoor(
        @Query("username") username: String,
        @Query("door_id") door_id: Int): Response<Door>


    @POST("/createAccount")
    suspend fun createAccount(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("pin") pin: Any,
        @Query("email") email:String): Response<Any?>


    @GET("/user/login")
    suspend fun login(
        @Query("username") username: String,
        @Query("password") password: String): Response<Any?>


    @POST("/user/shareLock")
    suspend fun shareLock(
        @Query("username") username: String,
        @Query("user_to_share") user_to_share: String,
        @Query("door_id") door_id: Int): Response<Any?>


    @POST("/user/updatePin")
    suspend fun updatePin(
        @Query("username") username: String,
        @Query("newPin") newPin:Int): Response<Any?>


    @POST("/user/updateComment")
    suspend fun updateComment(
        @Query("username") username: String,
        @Query("door_id") door_id: Int,
        @Query("newComment") newComment:String): Response<Any?>


    @POST("/user/password")
    suspend fun changePassword(
        @Query("username") username: String,
        @Query("newPassword") newPassword: String): Response<Any?>

    @POST("/user/username")
    suspend fun changeUsername(
        @Query("newUsername") newUsername: String,
        @Query("oldUsername") oldUsername: String): Response<Any?>

    @POST("/addNewLock")
    suspend fun addNewLock(
        @Query("username") username: String,
        @Query("app_code") app_code: Int): Response<Any?>

    @POST("/lock/changeLockName")
    suspend fun changeLockName(
        @Query("username") username: String,
        @Query("door_id") door_id:Int,
        @Query("new_door_name") new_door_name:String): Response<Any?>

    @POST("/user/changeNotificationPreference")
    suspend fun changeNotificationPreference(
        @Query("username") username: String): Response<Any?>

    @POST("/user/changeEmail")
    suspend fun changeEmail(
        @Query("username") username: String,
        @Query("newEmail") newEmail: String): Response<Any?>

    @POST("/user/deleteAccount")
    suspend fun deleteAccount(
        @Query("username") username: String): Response<Any?>

    //criar função para saber quais as portas que foram abertas
    //criar função para saber se pin introduzido foi errado

    @POST("/user/removeAccountFromDoor")
    suspend fun removeAccountFromDoor(
        @Query("username") username: String,
        @Query("username_to_be_removed") username_to_be_removed: String,
        @Query("door_id") door_id: Int): Response<Any?>


    @POST("/user/changeDoorState")
    suspend fun changeDoorState(
        @Query("username") username: String,
        @Query("door_id") door_id: Int): Response<Any?>


}





