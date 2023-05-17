package com.example.application

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import com.example.application.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

class MainAppActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_app)

        val bottomNav = findViewById<BottomNavigationView>(R.id.menu_bar)
        bottomNav.setOnItemSelectedListener(navListener)

        // as soon as the application opens the first fragment should
        // be shown to the user in this case it is algorithm fragment
        supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, MyLocksFragment()).commit()
    }



    private val navListener = NavigationBarView.OnItemSelectedListener { item ->
        // By using switch we can easily get the
        // selected fragment by using there id
        lateinit var selectedFragment: Fragment
        when (item.itemId) {
            R.id.action_lockers -> {
                selectedFragment = MyLocksFragment()
            }
            R.id.action_settings -> {
                selectedFragment = MyLocksFragment()
            }
            R.id.action_home -> {
                selectedFragment = ProfilePageFragment()
            }
        }
        // It will help to replace the
        // one fragment to other.
        supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, selectedFragment).commit()
        true
    }

}