package com.example.myapplication.model


    data class User (
        val username: String,
        val user_id: Int,
        val email: String,
        val password: String,
        val allow_notifications: Int,
        val active_locks:List<ActiveLocks> = listOf()
    )
