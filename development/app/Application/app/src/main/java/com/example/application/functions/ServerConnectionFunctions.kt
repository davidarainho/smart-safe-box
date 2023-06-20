package com.example.myapplication.functions

import android.util.Log
import com.example.myapplication.api.SimpleApi
import com.example.myapplication.model.LockConn
import com.example.myapplication.model.UserConn
import com.example.myapplication.objects.GetObject
import com.example.myapplication.objects.GetObject.getInstance
import java.io.IOException

class serverConnectionFunctions() {

    // Define API
    val api = GetObject.getInstance().create(SimpleApi::class.java)

    // Define Exceptions

    class ServerException(val code: Int): Exception("Server returned error code $code")


    /* Cria conta se username e email nao tiverem uma conta associada*/
    suspend fun createAccount(username: String, password: String, email: String, pincode: String): Boolean? {
        try {
            val checkUsername = api.returnUser(username)
            return if (!checkUsername.isSuccessful) {
                val checkEmail = api.checkEmail(email)
                if (!checkEmail.isSuccessful) {
                    val createAccount = api.createAccount(username, password, email, pincode)
                    if (createAccount.isSuccessful) {
                        true
                    } else {
                        //println(createAccount.errorBody()?.string())
                        false
                    }
                } else false //throw Exception("Email already exists.")
            } else false //throw Exception("Username already exists.")
        } catch (e: IOException) {
            return false
        }
    }


    suspend fun getUserConnLogin(username: String, password: String): UserConn? {
        return try {
            // Verify login
            val login = api.login(username, password)
            if (login.isSuccessful) {
                // Get user information
                val user = api.returnUser(username)
                if (user.isSuccessful) return user.body() else return null
            } else null
        } catch (e: IOException) {
            return null
        }
    }

    suspend fun getLockConnLogin(username: String, doorID : String): LockConn? {
        return try {
            // Verify login
            val retrievedLock = api.getActiveDoors(username)
            if (retrievedLock.isSuccessful) {
                // Get user information
                val lock = api.getLockConnObject(username, doorID)
                if (lock.isSuccessful) return lock.body() else return null
            } else null
        } catch (e: IOException) {
            return null
        }
    }


    suspend fun shareLock(username: String, user_to_share: String, door_id: String): Boolean {
        return try {
            val shareLock = api.shareDoor(username, user_to_share, door_id)
            if (shareLock.isSuccessful) return true else false
        } catch (e: IOException) {
            return false
        }
    }

    suspend fun changePin(username: String, newPin: String, oldPin: String): Boolean? {
        return try {
            val updatePin = api.updatePin(username,newPin, oldPin)
            if (updatePin.isSuccessful) return true else false
        } catch (e: IOException){
            return false
        }
    }

    suspend fun changeComment(username: String, door_id: String, newComment: String): Boolean {
        return try {
            val updateComment = api.updateComment(username,door_id, newComment)
            if (updateComment.isSuccessful) return true else false
        } catch (e: IOException){
            return false
        }
    }

    suspend fun changePassword(username: String, newPassword: String, oldPassword: String): Boolean {
        return try {
            val changePassword = api.changePassword(username, newPassword, oldPassword)
            if (changePassword.isSuccessful) return true else false
        } catch (e: IOException){
            return false
        }
    }

    suspend fun changeUsername(newUsername: String, oldUsername: String): Boolean {
        return try {
            val checkNewUsername = api.returnUser(newUsername)
            if (!checkNewUsername.isSuccessful) {
                val changeUsername = api.changeUsername(newUsername,oldUsername)
                if (changeUsername.isSuccessful) return true else false
            } else false
        } catch (e: IOException){
            return false
        }
    }

    // Rever esta funcao
    suspend fun addNewLock(username: String, app_code: String): List<String>? {
        return try {
            val addNewLock = api.addNewLock(username, app_code)
            if (addNewLock.isSuccessful) return addNewLock.body() else null
        } catch (e: IOException) {
            return null
        }
    }

    suspend fun changeLockName(username: String, new_door_name: String, door_id: String): Boolean {
        return try {
            val changeLockName = api.changeLockName(username, new_door_name, door_id)
            if (changeLockName.isSuccessful) return true else false
        } catch (e: IOException){
            return false
        }
    }

    suspend fun changeNotificationPreference(username: String): Boolean {
        return try {
            val changeNotificationPreference = api.changeNotificationPreference(username)
            if(changeNotificationPreference.isSuccessful) return true else false
        } catch (e: IOException){
            return false
        }
    }

    suspend fun changeEmail(username: String, newEmail: String): Boolean {
        return try {
            val checkNewEmail = api.checkEmail(newEmail)
            if (!checkNewEmail.isSuccessful) {
                val changeEmail = api.changeEmail(username,newEmail)
                if (changeEmail.isSuccessful) return true else false
            } else false
        } catch (e: IOException){
            return false
        }
    }

    suspend fun deleteAccount(username: String): Boolean {
        return try {
            val deleteAccount = api.deleteUser(username)
            if (deleteAccount.isSuccessful) return true else false
        } catch (e: IOException){
            return false
        }
    }

    suspend fun openDoor(username: String, door_id: String): Boolean {
        return try {
            val open = api.openDoor(username, door_id)
            if (open.isSuccessful) return true else false
        } catch (e: IOException){
            return false
        }
    }




/*    suspend fun checkNotifications(username: String): Int {
        return try{
            val checkErrorPin = api.checkErrorPin(username)
            if (checkErrorPin.isSuccessful) return 2
            else
        }
    }*/

    suspend fun removeAccountFromDoor(username: String, username_to_be_removed: String, door_id: String): Boolean? {
        return try {
            val removeAccountFromDoor = api.removeAccountFromDoor(username, username_to_be_removed, door_id)
            if (removeAccountFromDoor.isSuccessful) return true else false
        } catch (e: IOException){
            return false
        }
    }

/*    suspend fun changeDoorState(username: String, door_id: String): Boolean? {
        return try {
            val changeDoorState = api.changeDoorState(username, door_id)
            if (changeDoorState.isSuccessful) return true else false
        } catch (e: IOException){
            return false
        }
    }*/



}