package com.example.application.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.application.MyLocksFragmentDirections
import com.example.application.R
import com.example.application.data.user.User
import com.example.application.model.AccountInfo
import com.example.application.model.Userlockers
import java.util.concurrent.locks.Lock

class InfoAccountAdapter (
    private val dataset: List<User>?
) : RecyclerView.Adapter<InfoAccountAdapter.ItemViewHolder>() {

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.users_item)
        // Aqui adicionar para a outra caixa de texto
    }

    /**
     * Create new views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.drop_down_item, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val userinfo = dataset?.get(position)
        if (userinfo != null) {
            holder.textView.text = userinfo.username
        }
        // Aqui adicionar depois para atribuir à caixa de texto
        // Mais o .userEmail
    }

    /**
     * Return the size of your dataset (invoked by the layout manager)
     */
    override fun getItemCount() : Int {
        if(dataset != null){
            return dataset.size
        }
        return -1
    }
}