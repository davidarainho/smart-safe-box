package com.example.application.data.user

import androidx.room.Database
import androidx.room.RoomDatabase

@Database
    (
    entities = [User::class],
    version = 1
)

abstract class UserDatabase: RoomDatabase() {

    abstract val dao: UserDao






}