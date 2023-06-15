package com.example.myapplication.model
import com.google.gson.annotations.SerializedName

data class UserConn(
    val username: String,
    val password: String,
    val email: String?,
    val access_level: Int,
    val access_pin: String,
    val active_locks:List<ActiveLocks>? = listOf()

)

data  class ActiveLocks (
    val accessLevel : Int?,
    val lockId: String?
)
//
