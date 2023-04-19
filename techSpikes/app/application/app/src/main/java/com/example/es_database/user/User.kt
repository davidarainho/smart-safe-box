package com.example.es_database.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(

    val username: String,
    val email: String,
    val password: String,
    @PrimaryKey (autoGenerate = true)
    val user_id: Int=0
)
