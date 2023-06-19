package com.example.myapplication.functions

import com.example.myapplication.api.SimpleApi
import com.example.myapplication.model.correctDoor
import com.example.myapplication.objects.GetObject
import java.io.IOException

class ServerConnectionFunctions() {

    // Define API
    private val api: SimpleApi = GetObject.getInstance().create(SimpleApi::class.java)

    // Define Exceptions

    class ServerException(private val code: Int) : Exception("Server returned error code $code")

    suspend fun createAccount(username: String, password: String, pin: String, email: String): Boolean? {
        return try {
            val checkUsername = api.checkUsername(username)
            if (!checkUsername.isSuccessful) {
                val checkEmail = api.checkEmail(email)
                if (!checkEmail.isSuccessful) {
                    val createAccount = api.createAccount(username, password, pin, email)
                    if (createAccount.isSuccessful) return true else false
                } else throw Exception("Email already exists.")
            } else throw Exception("Username already exists.")
        } catch (e: IOException) {
            return false
        }
    }


    suspend fun login(username: String, password: String): Any? {
        return try {
            // Verify login
            val login = api.login(username, password)
            if (login.isSuccessful) {
                // Get user information
                val user = api.checkUsername(username)
                if (user.isSuccessful) return user.body() else return false
            } else false
        } catch (e: IOException) {
            return false
        }
    }


    suspend fun shareLock(username: String, user_to_share: String, door_id: Int): Boolean? {
        return try {
            val shareLock = api.shareLock(username, user_to_share, door_id)
            if (shareLock.isSuccessful) return true else false
        } catch (e: IOException) {
            return false
        }
    }

    suspend fun updatePin(username: String, newPin: Int): Boolean? {
        return try {
            val updatePin = api.updatePin(username, newPin)
            if (updatePin.isSuccessful) return true else false
        } catch (e: IOException) {
            return false
        }
    }

    suspend fun updateComment(username: String, door_id: Int, newComment: String): Boolean? {
        return try {
            val updateComment = api.updateComment(username, door_id, newComment)
            if (updateComment.isSuccessful) return true else false
        } catch (e: IOException) {
            return false
        }
    }

    suspend fun changePassword(username: String, newPassword: String): Boolean? {
        return try {
            val changePassword = api.changePassword(username, newPassword)
            if (changePassword.isSuccessful) return true else false
        } catch (e: IOException) {
            return false
        }
    }

    suspend fun changeUsername(newUsername: String, oldUsername: String): Boolean? {
        return try {
            val checkNewUsername = api.checkUsername(newUsername)
            if (!checkNewUsername.isSuccessful) {
                val changeUsername = api.changeUsername(newUsername, oldUsername)
                if (changeUsername.isSuccessful) return true else false
            } else throw Exception("Username already exists.")
        } catch (e: IOException) {
            return false
        }
    }

    suspend fun addNewLock(username: String, app_code: Int): Boolean? {
        return try {
            val addNewLock = api.addNewLock(username, app_code)
            if (addNewLock.isSuccessful) return true else false
        } catch (e: IOException) {
            return false
        }
    }

    suspend fun changeLockName(username: String, door_id: Int, new_door_name: String): Boolean? {
        return try {
            val changeLockName = api.changeLockName(username, door_id, new_door_name)
            if (changeLockName.isSuccessful) return true else false
        } catch (e: IOException) {
            return false
        }
    }

    suspend fun changeNotificationPreference(username: String): Boolean? {
        return try {
            val changeNotificationPreference = api.changeNotificationPreference(username)
            if (changeNotificationPreference.isSuccessful) return true else false
        } catch (e: IOException) {
            return false
        }
    }

    suspend fun changeEmail(username: String, newEmail: String): Boolean? {
        return try {
            val checkNewEmail = api.checkEmail(newEmail)
            if (!checkNewEmail.isSuccessful) {
                val changeEmail = api.changeEmail(username, newEmail)
                if (changeEmail.isSuccessful) return true else false
            } else throw Exception("Email already exists.")
        } catch (e: IOException) {
            return false
        }
    }

    suspend fun deleteAccount(username: String): Boolean? {
        return try {
            val deleteAccount = api.deleteAccount(username)
            if (deleteAccount.isSuccessful) return true else false
        } catch (e: IOException) {
            return false
        }
    }

//    suspend fun checkNotifications(username: String): Int {
//        return try{
//            val checkErrorPin = api.checkErrorPin(username)
//            if (checkErrorPin.isSuccessful) return 2
//            else
//        }
//    }

    suspend fun removeAccountFromDoor(
        username: String,
        username_to_be_removed: String,
        door_id: Int
    ): Boolean? {
        return try {
            val removeAccountFromDoor =
                api.removeAccountFromDoor(username, username_to_be_removed, door_id)
            if (removeAccountFromDoor.isSuccessful) return true else false
        } catch (e: IOException) {
            return false
        }
    }

    suspend fun changeDoorState(username: String, door_id: Int): Boolean? {
        return try {
            val changeDoorState = api.changeDoorState(username, door_id)
            if (changeDoorState.isSuccessful) return true else false
        } catch (e: IOException) {
            return false
        }
    }

    suspend fun lockInformation(username: String, door_id: Int): Any? {
        return try {
            val door = api.checkDoor(username, door_id)
            if (door.isSuccessful) {
                val locknameAsString = door.body()?.lock_name?.joinToString("") ?: return false
                val lockstateAsString = door.body()?.lock_state?.toString() ?: return false
                val dooridAsInt = door.body()?.door_id?.toInt() ?: return false
                val commentAsString = door.body()?.comment?.joinToString("") ?: return false

                val usersWithAccess = door.body()?.users_with_access ?: emptyList()

                return correctDoor(
                    lock_name = locknameAsString,
                    lock_state = lockstateAsString,
                    door_id = dooridAsInt,
                    last_access = door.body()?.last_access ?: "N/A",
                    user_last_access = door.body()?.user_last_access ?: "N/A",
                    number_of_users = door.body()?.number_of_users ?: 0,
                    users_with_access = usersWithAccess,
                    permission_level = door.body()?.permission_level ?: 0,
                    comment = commentAsString
                )
            } else false
        } catch (e: IOException) {
            return false
        }
    }
}
