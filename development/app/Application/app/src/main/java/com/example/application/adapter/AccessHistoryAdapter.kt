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
    private val dataset: Pair<List<String>?,List<String>?>
) : RecyclerView.Adapter<AccessHistoryAdapter.ItemViewHolder>() {

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val textName: TextView = view.findViewById(R.id.item_history)
        val textDate: TextView = view.findViewById(R.id.date)
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
        val lastAccessedUser = dataset.first?.get(position)
        val lastAccessedDate = dataset.second?.get(position)
        if (lastAccessedUser != null && lastAccessedDate != null) {
            holder.textName.text = lastAccessedUser
            holder.textDate.text = lastAccessedDate
            // userinfo.last_access
        }
        // Aqui adicionar depois para atribuir à caixa de texto
    }

    /**
     * Return the size of your dataset (invoked by the layout manager)
     */
    override fun getItemCount() : Int {
        if(dataset.first != null && dataset.second != null){
            return dataset.first!!.size
        }
        return 0
    }
}