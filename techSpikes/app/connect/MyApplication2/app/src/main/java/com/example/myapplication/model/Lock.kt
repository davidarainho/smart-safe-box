package com.example.myapplication.model

data class Lock (
    val lockID: String,
    val Name: String,
    val Location: String,
    val State: String,
    val maxNumUsers: Int
)
