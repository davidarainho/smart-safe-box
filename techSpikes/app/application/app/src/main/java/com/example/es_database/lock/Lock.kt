package com.example.es_database.lock

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class Lock(

    val lock_name: String,
    val last_access: String,
    val user_last_access: String,
    val number_of_users: Int,
    val comment: String,
    @PrimaryKey(autoGenerate = true)
    val lock_id: Int = 0

)
