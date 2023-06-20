package com.example.myapplication.model

data class UserConn(
    val username: String,
    val user_id : String,
    val password: String,
    val email: String,
    val allow_notifications : Int,
    val access_pin: String,
    val active_doors:List<String>? = listOf()
)
