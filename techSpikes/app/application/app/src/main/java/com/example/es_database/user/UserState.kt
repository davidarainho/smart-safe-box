package com.example.es_database.user

data class UserState(

    val users: List<User> = emptyList(),
    val username: String ="",
    val email: String ="",
    val password: String ="",
    // val allow_notifications: Int = 0,
    val isAddingUser: Boolean = false,
    val userSortType: UserSortType = UserSortType.USERNAME


)
