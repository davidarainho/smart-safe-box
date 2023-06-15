package com.example.myapplication.model


data class User (
    val username: String,
    val password: String,
    val email: String,
    val access_level: Int,
    val notifications: Int,
    val access_pin: String,
    val active_locks:List<Lock> = listOf()

)
