package com.example.myapplication


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.api.SimpleApi
import com.example.myapplication.functions.serverConnectionFunctions
import com.example.myapplication.model.User
import com.example.myapplication.objects.GetObject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


//login


/*
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val functionsConnection = serverConnectionFunctions()
        val username = "smart-safe-box.fabric-admin"
        val password = "mW!)u!Y7T3T^%dL"
        GlobalScope.launch {
            try {
                val login = functionsConnection.login(username,password)
                Log.d("example", "Successfully changed username. Active Locks are ${login}")
            } catch (e: Exception) {
                Log.d("example", "Login failed due to: ${e.message}")
            }
        }


    }
}

 */






class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val functionsConnection = serverConnectionFunctions()
        val username = "user2"
        val password = "pass2"
        val email = "email2"
        val pinLocks = "3203"
        GlobalScope.launch {
            try {
                val createUser = functionsConnection.createUser(username, password,email,pinLocks)
                Log.d("example", "$createUser")
            } catch (e: Exception) {
                Log.d("example", "Create User failed due to: ${e.message}")
            }
        }


    }
}







