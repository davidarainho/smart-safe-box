package com.example.es_database.userlock

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.es_database.lock.Lock
import com.example.es_database.user.User

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Lock::class,
            parentColumns = ["lock_id"],
            childColumns = ["lock_id"]
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"]
        )
    ]
)
data class UserLock(
    @PrimaryKey(autoGenerate = true)
    val user_lock_id: Int = 0,
    val lock_access_pin: String,
    val number_of_users: Int,
    val lock_id: Int,
    val user_id: Int
)