package com.example.myapplication.model

data class correctDoor (
    var lock_name: String,
    var lock_state: String,
    var door_id: Int,
    var last_access: String,
    var user_last_access: String,
    var number_of_users: Int,
    var users_with_access: List<String>,
    var permission_level: Int,
    var comment: String
)