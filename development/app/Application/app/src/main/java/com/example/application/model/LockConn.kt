package com.example.myapplication.model
import com.google.gson.annotations.SerializedName


data class LockConn (
    @SerializedName("lockID") val lockId: String,
    @SerializedName("Name") val name: String,
    @SerializedName("Location") val location: String,
    @SerializedName("State") val state: Int,
    @SerializedName("active_users") val activeUsers: List<String>,

    val lock_name: String,
    val lock_state: String,
    val lock_id: Int,
    val last_access: String,
    val user_last_access: String,
    val number_of_users: Int,
    val users_with_access: String,
    val permission_level: Int,
    val comment: String

)
