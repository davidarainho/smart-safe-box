package com.example.gui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonOne:TextView=findViewById(R.id.forgotPassword)
        buttonOne.setOnClickListener{
            val intent = Intent(this,RecoverPassword::class.java)
            startActivity(intent)
        }

        val usernameText:TextView = findViewById(R.id.username_text)
        val passwordText:TextView = findViewById(R.id.password_text)
        val buttonTwo:Button=findViewById(R.id.signIn)
        buttonTwo.setOnClickListener{
            if(usernameText.text.toString() == "batata@sopa.com" && passwordText.text.toString() == "qwerty") {
                val intent = Intent(this, ProfilePage::class.java)
                startActivity(intent)
            }
        }

        val buttonThree:TextView=findViewById(R.id.createAccount)
        buttonThree.setOnClickListener{
            val intent = Intent(this,CreateAccount::class.java)
            startActivity(intent)
        }

    }

}