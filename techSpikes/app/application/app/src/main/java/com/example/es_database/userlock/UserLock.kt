package com.example.es_database.userlock

import androidx.room.*
import com.example.es_database.lock.Lock
import com.example.es_database.user.User
/*
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
    val lock_id: Int,
    val user_id: Int
)*/


/*

@Entity(
    tableName = "UserLock",
    indices = [Index("lock_id"), Index("user_id")],
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
    val lock_id: Int,
    val user_id: Int
)


data class UserLockWithRelations(
    @Embedded
    val userLock: UserLock,
    @Relation(
        parentColumn = "lock_id",
        entityColumn = "lock_id"
    )
    val lock: Lock?,
    @Relation(
        parentColumn = "user_id",
        entityColumn = "user_id"
    )
    val user: User?
)
*/

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id_fk"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Lock::class,
            parentColumns = ["lock_id"],
            childColumns = ["lock_id_fk"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id_fk"]),
        Index(value = ["lock_id_fk"])
    ]
)
data class UserLock(
    @ColumnInfo(name = "user_id_fk")
    val user_id: Int,
    @ColumnInfo(name = "lock_id_fk")
    val lock_id: Int,
    val lock_access_pin: String,
    @PrimaryKey(autoGenerate = true)
    val userLockId: Int = 0
)

data class UserLockWithRelations(
    @Embedded
    val userLock: UserLock,
    @Relation(
        parentColumn = "lock_id_fk",
        entityColumn = "lock_id"
    )
    val lock: Lock?,
    @Relation(
        parentColumn = "user_id_fk",
        entityColumn = "user_id"
    )
    val user: User?
)
