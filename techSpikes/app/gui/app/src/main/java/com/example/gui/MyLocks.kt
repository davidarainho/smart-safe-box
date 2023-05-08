package com.example.gui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.gui.adapter.ItemAdapter
import com.example.gui.data.Datasource
import com.example.gui.model.Affirmation


class MyLocks  : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_locks)

        // Initialize data.
        val myDataset = Datasource().loadAffirmations()
        var myList: List<Affirmation>
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = ItemAdapter(this, myDataset)

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true)

        val search : EditText = findViewById(R.id.username_text)
        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                myList = if(s.isNotEmpty()) {
                    myDataset.filter { lock -> getString(lock.stringResourceId).contains(s.toString(), ignoreCase = true) }
                }else{
                    myDataset
                }
                recyclerView.adapter = ItemAdapter(applicationContext, myList)

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })



    }



}