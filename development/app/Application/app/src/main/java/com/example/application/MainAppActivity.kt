package com.example.application

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import android.os.Handler
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainAppActivity : AppCompatActivity() {


    private lateinit var navController : NavController

    private lateinit var name : String

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

        startPrintLoop()
    }
    private fun startPrintLoop() {
        printRunnable = object : Runnable {
            override fun run() {

                println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!Printing from the subroutine!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
                handler.postDelayed(this, 5000)
            }
        }
        handler.postDelayed(printRunnable, 5000)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(printRunnable)
    }
}