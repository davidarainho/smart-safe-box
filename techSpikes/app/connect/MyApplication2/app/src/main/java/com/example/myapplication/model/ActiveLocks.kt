package com.example.myapplication.model
import com.google.gson.annotations.SerializedName

data class ActiveLocks(
    @SerializedName("access_level")
    val accessLevel : Int?,
    @SerializedName("door_id")
    val door_id: String?
)