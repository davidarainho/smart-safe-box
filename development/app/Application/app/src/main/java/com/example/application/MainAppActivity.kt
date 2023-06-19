package com.example.application

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import android.os.Handler
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.getSystemService
import com.example.application.data.LockDBSingleton
import com.example.application.data.UserAndLock.UserAndLockDao
import com.example.application.data.UserAndLockDBSingleton
import com.example.application.data.UserDBSingleton
import com.example.application.data.lock.LockDao
import com.example.application.data.user.UserDao
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.*
import java.lang.Runnable


class MainAppActivity : AppCompatActivity() {


    private lateinit var navController : NavController

    private lateinit var name : String

    private val CHANNEL_ID = "channel_id_teste"
    private val notificationIdWrong = 101
    private val notificationIdState = 200

    private val handler = Handler()

    private lateinit var printRunnable: Runnable

    //private val sharedViewModel: AppViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_app)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mainContainer) as NavHostFragment
        navController = navHostFragment.navController

        navController.setGraph(R.navigation.nav_graph_app, intent.extras) // Tirar no xml app:navGraph="@navigation/nav_graph_app" ?

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.menu_bar)
        setupWithNavController(bottomNavigationView, navController)

        createNotificationChannel()
        startPrintLoop()
    }
    private fun startPrintLoop() {
        var flagWrongPin : Boolean = false
        var flagChangedLockState : Boolean = false



        // Usar query ao servidor para receber lista de doors para determinada flag
        // (Erro ao inserir código ou alteração do estado do lock)

        // Concatenar as doors numa string e colocar em lockname1 e lockname2

        // Alterar os estados das doors na base de dados
        val lockDatabaseSingleton = LockDBSingleton.getInstance(this)
        val lockDao : LockDao? = lockDatabaseSingleton!!.getAppDatabase().lockDao()


        // Receber lista
        var lockname1 : String = "default"
        var lockname2 : String = "default"

        printRunnable = object : Runnable {
            override fun run() {
                // Pedidos para verificar alteracoes (Pin Errado / Estado Lock)

                if (flagWrongPin){
                    sendNotificationWrongPin(lockname1)
                    flagWrongPin = false
                }

                if (flagChangedLockState){
                    sendNotificationState(lockname2)
                    flagChangedLockState = false
                }

                handler.postDelayed(this, 15000)
            }
        }
        handler.postDelayed(printRunnable, 15000)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val name = "My Notification"
        val descriptionText = "Notificacoes"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun sendNotificationWrongPin(lockname : String){
        val message : String =
            "The wrong pin was inserted for doors $lockname. It will be blocked for 1 minute."
        val builder = NotificationCompat.Builder(this,CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_lock_24)
            .setContentTitle("Wrong Pin Inserted")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)){
            if (ActivityCompat.checkSelfPermission(
                    this@MainAppActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(notificationIdWrong,builder.build())
        }
    }

    private fun sendNotificationState(lockname : String){
        val message : String =
            "Lock $lockname was opened."
        val builder = NotificationCompat.Builder(this,CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_lock_24)
            .setContentTitle("Changed State of Lock.")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)){
            if (ActivityCompat.checkSelfPermission(
                    this@MainAppActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(notificationIdState,builder.build())
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    suspend fun deleteData() {
        val lockDatabase = LockDBSingleton.getInstance(this)
        val lockDao: LockDao? = lockDatabase!!.getAppDatabase().lockDao()

        val userDatabase = UserDBSingleton.getInstance(this)
        val userDao: UserDao = userDatabase!!.getAppDatabase().userDao()

        val userAndLockDatabase = UserAndLockDBSingleton.getInstance(this)
        val userLockDao: UserAndLockDao? = userAndLockDatabase!!.getAppDatabase().userAndLockDao()

        withContext(Dispatchers.IO) {

            if (lockDao != null) {
                lockDao.deleteLockData()
            }
            userDao.deleteUserData()

            if (userLockDao != null) {
                userLockDao.deleteUserLockData()
            }

        }
    }

    override fun onDestroy() {

        super.onDestroy()
        handler.removeCallbacks(printRunnable)
    }

    override fun onStop() {

        super.onStop()

        if (isFinishing) {
            GlobalScope.launch {
                deleteData()
            }
        }
    }
}