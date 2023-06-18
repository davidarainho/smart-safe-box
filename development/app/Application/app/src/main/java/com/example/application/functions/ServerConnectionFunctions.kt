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


    //testadas

    suspend fun createUser(username: String, password: String, email: String, accessPin:String): Boolean? {
        return try {
            val  accessLevel0 = 0
            val checkUsername = api.getUser(username)
            if (!checkUsername.isSuccessful) {
                val checkEmail = api.getEmail(username)
                if (!checkEmail.isSuccessful) {
                    val createUser = api.createUser(username, password, email, accessLevel0,accessPin)
                    if (!createUser.isSuccessful) throw ServerException(createUser.code()) else return true
                } else throw Exception("Email already exists.")
            } else throw Exception("Username already exists.")
        } catch (e: IOException) {
            throw Exception("Unable to connect to the server.")
        }
    }




    suspend fun login(username: String, password: String): UserConn? {
        return try {
            // Verify login
            val responseLogin = api.login(username, password)
            if (responseLogin.isSuccessful) {
                val user = api.getUser(username)
                if (user.isSuccessful) return user.body() else throw ServerException(user.code())
            } else {throw ServerException(responseLogin.code())}
        } catch (e: IOException) {
            e.printStackTrace()
            throw Exception("Unable to connect to the server")
        }
    }



    suspend fun firstInteraction(username:String, appcode:String): Boolean? {
        return try {
            val firstInteraction = api.firstInteraction(username, appcode)
            if (firstInteraction.isSuccessful) true else throw ServerException(firstInteraction.code())
        } catch (e: IOException) {
            throw Exception("Unable to connect to the server.")
        }
    }


    suspend fun openLocks(username:String): Boolean? {
        return try {
            val openLocks = api.openLocks(username)
            if (openLocks.isSuccessful) true else throw ServerException(openLocks.code())
        } catch (e: IOException) {
            throw Exception("Unable to connect to the server.")
        }
    }

    suspend fun allocateLock(username: String, lockID: String, accessLevel: Int): Boolean? {
        return try {
            val allocateLock = api.allocateLock(username, lockID, accessLevel)
            if (allocateLock.isSuccessful) true else throw ServerException(allocateLock.code())
        } catch (e: IOException) {
            throw Exception("Unable to connect to the server.")
        }
    }








    suspend fun updateUserPin(username: String, oldPin: String, newPin: String): Boolean? {
        return try {
            val updateUserPin = api.updateUserPin(username,newPin,oldPin)
            if (updateUserPin.isSuccessful) true else throw ServerException(updateUserPin.code())
        } catch (e: IOException){
            throw  Exception("Unable to connect to the server.")
        }
    }


    suspend fun updateLockLocation(lockID: String, newLocation: String): Boolean? {
        return try {
            val updateLockLocation = api.updateLockLocation(lockID, newLocation)
            if (updateLockLocation.isSuccessful) true else throw ServerException(updateLockLocation.code())
        } catch (e: IOException) {
            throw Exception("Unable to connect to the server.")
        }
    }

    suspend fun updateLockName(lockID: String, newName: String): Boolean? {
        return try {
            val updateLockName = api.updateLockName(lockID, newName)
            if (updateLockName.isSuccessful) true else throw ServerException(updateLockName.code())
        }  catch (e: IOException){
            throw  Exception("Unable to connect to the server.")
        }
    }



    suspend fun deallocateLock(username: String, lockID: String): Boolean?{
        return try {
            val deallocateLock = api.deallocateLock(username,lockID)
            if (deallocateLock.isSuccessful) true else throw ServerException(deallocateLock.code())
        } catch (e: IOException){
            throw  Exception("Unable to connect to the server.")
        }
    }





    // fazem a troca, mas no return dão a excpetion for some reason

    suspend fun changePassword(username: String, oldPassword: String, newPassword: String): Boolean? {
        return try {
            val response = api.updateUserPassword(username,oldPassword,newPassword)
            if (response.isSuccessful) {
                return true
            } else {
                throw ServerException(response.code())
            }
        } catch (e: IOException){
            throw  Exception("Unable to connect to the server.")
        }
    }




    suspend fun updateUserUsername(oldUsername: String, newUsername: String): Boolean? {
        return try {
            val checkNewUsername = api.getUser(newUsername)
            if (!checkNewUsername.isSuccessful) {
                val updateUserUsername = api.updateUserUsername(oldUsername,newUsername)
                if (updateUserUsername.isSuccessful) return true else throw ServerException(updateUserUsername.code())
            } else throw Exception("Username already exists.")
        } catch (e: IOException){
            throw  Exception("Unable to connect to the server.")
        }
    }



    // a testar
    suspend fun getLock(username: String, lockID: String): LockConn? {
        try {
            val response = api.getLock(username, lockID)
            if (response.isSuccessful) {
                return response.body()
            } else {
                throw ServerException(response.code())
            }
        } catch (e: IOException) {
            e.printStackTrace()
            throw Exception("Unable to connect to the server")
        }
    }









}