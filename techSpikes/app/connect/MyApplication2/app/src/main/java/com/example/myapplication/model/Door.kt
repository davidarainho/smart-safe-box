package com.example.myapplication.model

data class Door (
    val lock_name: String,
    val lock_state: String,
    val lock_id: Int,
    val last_access: String,
    val user_last_access: String,
    val number_of_users: Int,
    val users_with_access: List <String>,
    val permission_level: Int,
    val comment: String
)