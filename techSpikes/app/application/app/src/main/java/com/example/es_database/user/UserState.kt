package com.example.es_database.user

import com.example.es_database.SortType

data class UserState(

    val users: List<User> = emptyList(),
    val username: String ="",
    val email: String ="",
    val password: String ="",
    val isAddingUser: Boolean = false,
    val sortType: SortType = SortType.USERNAME


)
