package com.example.myapplication


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.api.SimpleApi
import com.example.myapplication.functions.ServerConnectionFunctions
import com.example.myapplication.model.User
import com.example.myapplication.objects.GetObject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.example.myapplication.model.Door









class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val functionsConnection = ServerConnectionFunctions()
        val username = "user1"
        val password = "pass1"
        val username2= "user2"

        //criar user


/*
        val email = "email1@feup.pt"
        val accessPin = "3203"
        GlobalScope.launch {
            try {
                val createUser = functionsConnection.createUser("user1", "pass1","email1","1111")
                Log.d("example", "$createUser")
            } catch (e: Exception) {
                Log.d("example", "Create User failed due to: ${e.message}")
            }
        }

 */







//login and get lock information
/*
        GlobalScope.launch {
            try {
                val user = functionsConnection.login("jtasso", "pass")
                Log.d("example", "$user")
            } catch (e: Exception) {
                Log.d("example", "User Login failed due to: ${e.message}")
            }
        }

 */












/*

//inicialização
    val appcode = "1111"
        GlobalScope.launch {
        try {
            val user = functionsConnection.firstInteraction("jtasso",appcode)
            Log.d("example", "$user")
        } catch (e:Exception) {
            Log.d("example", "error")
        }
    }

 */



//open User
/*

        GlobalScope.launch {
        try {
            val user = functionsConnection.openLocks("jtasso")
            Log.d("example", "$user")
        } catch (e:Exception) {
            Log.d("example", "error")
        }
    }

 */







/*

        GlobalScope.launch {
            try {
                val user = functionsConnection.updateUserUsername("smart-safe-box.fabric-admin","user1")
                Log.d("example", "boy: $user")
            } catch(e:Exception) {
                Log.d("example", "${e.message}")
            }
        }

 */






/*





        val passwword2 = "pass2"

        GlobalScope.launch {
            try {
                val user = functionsConnection.changePassword("user1","pass1","pass2")
                Log.d("example", "boy: $user")
            } catch(e:Exception) {
                Log.d("example", "${e.message}")
            }
        }


 */




/*

        GlobalScope.launch {
            try {
                val user = functionsConnection.allocateLock("user2","Lock2",1)
                Log.d("example", "boy: $user")
            } catch(e:Exception) {
                Log.d("example", "${e.message}")
            }
        }

 */


/*

        GlobalScope.launch {
            try {
                val user = functionsConnection.deallocateLock("user3","Lock2")
                Log.d("example", "boy: $user")
            } catch(e:Exception) {
                Log.d("example", "${e.message}")
            }
        }

 */




/*


        GlobalScope.launch {
            try {
                val user = functionsConnection.updateUserPin("user2","1111","2222")
                Log.d("example", "boy: $user")
            } catch(e:Exception) {
                Log.d("example", "${e.message}")
            }
        }

 */





/*
        GlobalScope.launch {
            try {
                val user = functionsConnection.updateLockLocation("Lock2","Onde a Sol nao Chega")
                Log.d("example", "boy: $user")
            } catch(e:Exception) {
                Log.d("example", "${e.message}")
            }
        }

 */



/*
        GlobalScope.launch {
            try {
                val user = functionsConnection.updateLockName("Lock2","Narnia")
                Log.d("example", "boy: $user")
            } catch(e:Exception) {
                Log.d("example", "${e.message}")
            }
        }

 */




    }
}







