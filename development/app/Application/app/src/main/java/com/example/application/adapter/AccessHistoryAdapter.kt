package com.example.application.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.application.R
import com.example.application.data.lock.Lock
import com.example.application.model.AccountInfo

class AccessHistoryAdapter (
    private val dataset: List<Lock>?
) : RecyclerView.Adapter<AccessHistoryAdapter.ItemViewHolder>() {

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.item_history)
        // Aqui adicionar para a outra caixa de texto
    }

    /**
     * Create new views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_access_history_item, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val userinfo = dataset?.get(position)
        if (userinfo != null) {
            holder.textView.text = userinfo.user_last_access
            // userinfo.last_access
        }
        // Aqui adicionar depois para atribuir à caixa de texto
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