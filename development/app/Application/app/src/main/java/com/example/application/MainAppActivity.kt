package com.example.application

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.application.model.AppViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.activityViewModels


class MainAppActivity : AppCompatActivity() {


    private lateinit var navController : NavController

    private lateinit var name : String

    //private val sharedViewModel: AppViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_app)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mainContainer) as NavHostFragment
        navController = navHostFragment.navController

        navController.setGraph(R.navigation.nav_graph_app, intent.extras) // Tirar no xml app:navGraph="@navigation/nav_graph_app" ?

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.menu_bar)
        setupWithNavController(bottomNavigationView, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}