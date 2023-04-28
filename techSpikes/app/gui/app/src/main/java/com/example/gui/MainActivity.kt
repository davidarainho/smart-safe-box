package com.example.gui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button:TextView=findViewById(R.id.createAccount)
        button.setOnClickListener(){
            val intent = Intent(this,CreateAccount::class.java)
            startActivity(intent)
        }
    }

}