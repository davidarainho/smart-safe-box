package com.example.myapplication.model
import com.google.gson.annotations.SerializedName


data class LockConn (
    @SerializedName("lockID") val lockId: String,
    @SerializedName("Name") val name: String,
    @SerializedName("Location") val location: String,
    @SerializedName("State") val state: Int,
    @SerializedName("active_users") val activeUsers: List<String>
)
