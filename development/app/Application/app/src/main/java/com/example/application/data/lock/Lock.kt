package com.example.application.data.lock

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity (tableName = "Lock", indices = [Index(value = ["eKey"], unique = true)])
data class Lock(

    val lock_name: String,
    val last_access: String,
    val user_last_access: String,
    val number_of_users: Int,
    val comment: String,
    val eKey: String?,
    val lock_state: String,
    @PrimaryKey(autoGenerate = true)
    val lock_id: Int = 0

)
