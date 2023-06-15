package com.example.myapplication.functions

import android.util.Log
import com.example.myapplication.api.SimpleApi
import com.example.myapplication.model.Lock
import com.example.myapplication.model.User
import com.example.myapplication.objects.GetObject
import com.example.myapplication.objects.GetObject.getInstance
import java.io.IOException



class serverConnectionFunctions() {

    // Define API
    val api = GetObject.getInstance().create(SimpleApi::class.java)

    // Define Functions GET

    class ServerException(val code: Int): Exception("Server returned error code $code")

/*

    suspend fun login(username: String, password: String): User? {
        return try {
            // Verify login
            val responseLogin = api.login(username, password)
            if (!responseLogin.isSuccessful) throw ServerException(responseLogin.code())
            // Verify email
            val responseEmail = api.getEmail(username)
            if (!responseEmail.isSuccessful) throw ServerException(responseEmail.code())
            val email = responseEmail.body()?.email
            // Verify active locks
            Log.d("example", "correu até aqui")


            //standby
            val responseActiveLocks = api.getActiveLocks(username)
            if (!responseActiveLocks.isSuccessful) throw ServerException(responseActiveLocks.code())



            Log.d("example","correu aqui")
            // Convert active locks to list
            val activeLocks: List<Lock> = responseActiveLocks.body() ?:  listOf()

            // Detailed active locks
            val detailedActiveLocks = mutableListOf<Lock>()
            for (lock in activeLocks) {
                val responseLock = api.getLock(username, lock.lockID)
                if (!responseLock.isSuccessful) throw ServerException(responseLock.code())
                responseLock.body()?.let { detailedActiveLocks.add(it) }
            }
            return User(username, password, "email1",0,"0930", detailedActiveLocks)

        } catch (e: IOException) {
            e.printStackTrace()
            throw Exception("Unable to connect to the server")
        }
    }








 */




    suspend fun createUser(username: String, password: String, email: String, access_pin:String): Boolean? {
        return try {
            val  access_level = 0
            val checkUsername = api.getUser(username)
            if (!checkUsername.isSuccessful) {
                val checkEmail = api.getEmail(username)
                if (!checkEmail.isSuccessful) {
                    val createUser = api.createUser(username, password, email, access_level,access_pin)
                    if (!createUser.isSuccessful) throw ServerException(createUser.code()) else return true
                } else throw Exception("Email already exists.")
            } else throw Exception("Username already exists.")
        } catch (e: IOException) {
            throw Exception("Unable to connect to the server.")
        }
    }

/*

    suspend fun changeUsername(oldUsername: String, newUsername: String): Boolean? {
        return try {
            val checkNewUsername = api.getUser(username)
            if (!checkNewUserName.isSuccessful) {
                val response = api.updateUserUsername(oldUsername,newUsername)
                if (response.isSuccessful) {
                    return true
                } else {throw ServerException(createUser.code()) }
            } else throw Expection("Username already exists.")
        } catch (e: IOException){
            throw  Exception("Unable to connect to the server.")
        }
    }


    suspend fun changePassword(username: String, oldPassword: String, newPassword: String): Boolean? {
        return try {
            val checkNewUsername = api.getUser(username)
            if (!checkNewUserName.isSuccessful) {
                val response = api.updateUserUsername(oldUsername,newUsername)
                if (response.isSuccessful) {
                    return true
                } else {throw ServerException(createUser.code()) }
            } else throw Expection("Username already exists.")
        } catch (e: IOException){
            throw  Exception("Unable to connect to the server.")
        }
    }


    suspend fun updatePinLock(username: String, oldPin: String, newPin, String): Boolean? {
        return try {
            val updatePinLock = api.udpatePinLock(username,newPin,oldPin)
            if (updatePinLock.isSuccessful) true else throw ServerException(updatePinLock.code())
        } catch (e: IOException){
            throw  Exception("Unable to connect to the server.")
        }
    }

    suspend fun allocateLock(username: String, lockID: String, accessLevel: Int): Boolean?{
        return try {
            val allocateLock = api.allocateLock(username,lockID,accessLevel)
            if (allocateLock.isSuccessful) true else throw ServerException(allocateLock.code())
        }
    } catch (e: IOException){
        throw  Exception("Unable to connect to the server.")
    }


    suspend fun deAllocateLock(username: String, lockID: String): Boolean? {
        val deAllocateLock

    } catch (e: IOException){
        throw  Exception("Unable to connect to the server.")
    }

    suspend fun updateLockLocation() {

    } catch (e: IOException){
        throw  Exception("Unable to connect to the server.")
    }

    suspend fun updateLockName() {

    } catch (e: IOException){
        throw  Exception("Unable to connect to the server.")
    }

    suspend fun openLock() {

    } catch (e: IOException){
        throw  Exception("Unable to connect to the server.")
    }

    suspend fun initialization() {

    } catch (e: IOException){
        throw  Exception("Unable to connect to the server.")
    }


 */



}