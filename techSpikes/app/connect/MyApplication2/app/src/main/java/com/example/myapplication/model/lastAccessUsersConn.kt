package com.example.myapplication.model

import com.google.gson.annotations.SerializedName

data class lastAccessUsersConn (
    @SerializedName("username") val username: String,
    @SerializedName("time") val time: String
)
