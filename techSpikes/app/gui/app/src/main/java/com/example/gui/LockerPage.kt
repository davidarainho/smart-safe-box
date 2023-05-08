package com.example.gui

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LockerPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.locker_page)

        var state = 0
        val imageView: ImageView = findViewById(R.id.imageView)
        imageView.setOnClickListener{
            if(state == 0){
                imageView.setImageResource(R.drawable.lock_open)
                state = 1
            }else if(state == 1){
                imageView.setImageResource(R.drawable.lock_closed)
                state = 0
            }
        }

    }

}