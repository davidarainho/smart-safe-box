package com.example.myapplication.model
import com.google.gson.annotations.SerializedName


data class LockConn (
    val lock_name: String,
    val lock_state: String,
    val lock_id: String,
    val last_access: String,
    val user_last_access: String,
    val number_of_users: Int,
    val users_with_access: List<String>,
    val permission_level: Int,
    val comment: String
)
