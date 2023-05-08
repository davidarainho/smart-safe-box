package com.example.gui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.locks.Lock

class ProfilePage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_page)

        val buttonOne: TextView =findViewById(R.id.logout_button)
        buttonOne.setOnClickListener{
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        val buttonTwo: TextView =findViewById(R.id.your_email)
        buttonTwo.setOnClickListener{
            val intent = Intent(this,MyLocks::class.java)
            startActivity(intent)
        }

        val buttonThree: TextView =findViewById(R.id.change_password)
        buttonThree.setOnClickListener{
            val intent = Intent(this,LockerPage::class.java)
            startActivity(intent)
        }
    }

}