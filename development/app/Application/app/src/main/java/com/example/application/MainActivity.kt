package com.example.application

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.application.data.LockDBSingleton
import com.example.application.data.UserAndLockDBSingleton
import com.example.application.data.UserDBSingleton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        LockDBSingleton.getInstance(getApplicationContext());
        UserDBSingleton.getInstance(getApplicationContext());
        UserAndLockDBSingleton.getInstance(getApplicationContext());


    }
}