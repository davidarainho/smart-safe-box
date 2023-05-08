package com.example.es_database.lock


data class LockState(

    val locks: List<Lock> = emptyList(),
    val lock_name: String ="",
    val last_access: String ="",
    val user_last_access: String ="",
    val number_of_users: Int =0,
    val isAddingLock: Boolean = false,
    val lockSortType: LockSortType = LockSortType.LOCK_NAME,
    val errorMessage: String = ""

)
