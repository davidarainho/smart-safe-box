package com.example.es_database.lock

sealed interface LockEvent {

    // event == user action

    object SaveLock: LockEvent
    data class SetLockName(val lockName: String): LockEvent
    data class SetLastAccess(val lastAccess: String): LockEvent
    data class SetUserLastAccess(val userLastAccess: String): LockEvent
    data class SetNumberOfUsers(val numberOfUsers: Int): LockEvent

    data class SortLocks(val lockSortType: LockSortType): LockEvent
    data class DeleteLock(val lock: Lock): LockEvent

    object ShowDialog: LockEvent
    object HideDialog: LockEvent
}
