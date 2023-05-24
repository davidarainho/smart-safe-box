package com.example.application.data.user

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "User", indices = [Index(value = ["email"], unique = true)]) // garante que o email é unico
data class User(

    val username: String,
    val email: String,
    val password: String,
    val allow_notifications: Int,
    @PrimaryKey (autoGenerate = true)
    val user_id: Int=0

)
