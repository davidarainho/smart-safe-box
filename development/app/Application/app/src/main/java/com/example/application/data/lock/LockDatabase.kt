package com.example.application.data.lock

import androidx.room.Database
import androidx.room.RoomDatabase

@Database
    (
    entities = [Lock::class],
    version = 1
)

abstract class LockDatabase: RoomDatabase() {

    abstract val dao: LockDao


}