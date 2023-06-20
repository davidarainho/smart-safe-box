package com.example.myapplication.api

import com.example.application.data.user.User
import com.example.myapplication.model.ActiveLocksConn
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

    //testadas e confirmadas

    data class EmailResponse(val email: String)
    data class UpdateUsernameResponse(val success: String)
    data class UpdatePasswordResponse(val success: String)

    /************************************************************************/
    /**************************** Funcoes Auxil. ****************************/
    /************************************************************************/

    /******************************** GET ***********************************/
    @GET("/usernames")
    suspend fun getAllUsernames(): Response<String?>

    @GET("/user")
    suspend fun checkUsername(@Query("username") username: String): Response<String?>

    @GET("/user/login")
    suspend fun login(
        @Query("username") username: String,
        @Query("password") password: String
    ): Response<UserConn?>

    @GET("/user/email")
    suspend fun checkEmail(@Query("email") email: String): Response<String?>

    @GET("/user/active_locks")
    suspend fun getActiveDoors(
        @Query("username") username: String
    ): Response<ActiveLocksConn?>

    @GET("/door")
    suspend fun getLockConnObject(
        @Query("door_id") door_id: String,
        @Query("username") username: String
    ): Response<LockConn?>




    /******************************** POST **********************************/
    @POST("/add_user")
    suspend fun createAccount(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("email") email:String,
        @Query("pincode") pincode: String,
    ): Response<String?>

    @POST("/delete_user")
    suspend fun deleteUser(
        @Query("username") username: String
    ): Response<String?>

    @POST("/user/username")
    suspend fun changeUsername(
        @Query("new_username") new_username: String,
        @Query("old_username") old_username: String
    ): Response<String?>

    @POST("/user/password")
    suspend fun changePassword(
        @Query("username") username: String,
        @Query("new_password") new_password: String,
        @Query("old_password") old_password: String
    ): Response<String?>

    @POST("/user/email")
    suspend fun changeEmail(
        @Query("username") username: String,
        @Query("new_email") new_email: String
    ): Response<String?>

    @POST("/user/updateUserPin")
    suspend fun updatePin(
        @Query("username") username : String,
        @Query("new_pin") new_pin : String,
        @Query("old_pin") old_pin : String
    ): Response<String?>

    @POST("/door/update_comment")
    suspend fun updateComment(
        @Query("username") username: String,
        @Query("door_id") door_id: String,
        @Query("new_comment") new_comment:String
    ): Response<String?>

/*    @POST("/user/share_door")
    suspend fun shareDoor(
        @Query("user_to_share") user_to_share: String,
        @Query("door_id") door_id: String,
        @Query("access_level") access_level: String
    ): Response<String?>*/

    @POST("/user/door_access_level")
    suspend fun updateAccessLevel(
        @Query("username") username: String,
        @Query("new_access_level") new_access_level: String,
        @Query("door_id") door_id: String
    ): Response<String?>

    @POST("/lock/update_door_name")
    suspend fun changeLockName(
        @Query("username") username : String,
        @Query("new_door_name") new_door_name : String,
        @Query("door_id") door_id : String
    ): Response<String?>

    @POST("/add_new_device")
    suspend fun addNewLock(
        @Query("username") username: String,
        @Query("app_code") app_code: String
    ): Response<LockConn?>

    @POST("/user/change_notification_preference")
    suspend fun changeNotificationPreference(
        @Query("username") username: String
    ): Response<String?>



/*    @POST("/user/removeAccountFromDoor")
    suspend fun removeAccountFromDoor(
        @Query("username") username: String,
        @Query("username_to_be_removed") username_to_be_removed: String,
        @Query("door_id") door_id: Int
    ): Response<String?>*/


}





