package com.example.myapplication.model

data class UserConn(
    val username: String,
    val password: String,
    val email: String?,
    val access_level: Int,
    val access_pin: String,
    val active_locks:List<ActiveLocksConn>? = listOf()
)
