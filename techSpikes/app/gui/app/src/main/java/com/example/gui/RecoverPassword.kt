package com.example.gui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RecoverPassword : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recover_password)

        //val buttonOne: TextView =findViewById(R.id.forgotPassword)
        //buttonOne.setOnClickListener(){
        //    val intent = Intent(this,RecoverPassword::class.java)
        //    startActivity(intent)
        //}
        val buttonTwo:TextView=findViewById(R.id.returnHelloPage)
        buttonTwo.setOnClickListener(){
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }


    }

}