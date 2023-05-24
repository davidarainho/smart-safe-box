package com.example.application.data.userLock

import androidx.room.*
import com.example.application.data.lock.Lock
import com.example.application.data.user.User


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
    val permission_level: Int,
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
