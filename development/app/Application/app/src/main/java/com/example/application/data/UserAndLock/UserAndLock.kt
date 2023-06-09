package com.example.application.data.UserAndLock

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "UserAndLock")

data class UserAndLock(

    val user_id: Int,
    val lock_id: Int,
    val lock_access_pin: String,
    val permission_level: Int,
    @PrimaryKey(autoGenerate = false)
    val userLockId: Int

)