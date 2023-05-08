package com.example.es_database.user

sealed interface UserEvent {

    // event == user action

    object SaveUser: UserEvent
    data class SetUsername (val username: String): UserEvent
    data class SetEmail (val email: String): UserEvent
    data class SetPassword (val password: String): UserEvent

    data class SortUsers(val userSortType: UserSortType): UserEvent
    data class DeleteUser(val user: User): UserEvent

    object ShowDialog: UserEvent
    object HideDialog: UserEvent


}